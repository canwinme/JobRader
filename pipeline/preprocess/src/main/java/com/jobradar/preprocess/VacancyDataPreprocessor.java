package com.jobradar.preprocess;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.*;

/**
 * data.go.kr 사업체노동력조사 원자료(CP949)를
 * VacancyAggregationJob(Hadoop)이 읽을 수 있는 형식으로 정제함.
 *
 * 원본 구조(가로형):
 *   지역별,산업별,항목,단위,2022. 1/2,2022. 2/2,...,2025. 2/2
 *   서울특별시종로구,광업.제조업(B,C),전체종사자,명,1234,1250,...
 *   서울특별시종로구,광업.제조업(B,C),빈일자리율,%,0.8,0.9,...
 *   (지역x산업 조합마다 항목 4개가 별도 행으로 흩어져 있음)
 *
 * 출력(세로형, Hadoop 입력):
 *   지역,산업코드,연도,반기,전체종사자,빈일자리율,입직률,이직률
 *   서울특별시종로구,BC,2022,1,1234,0.8,3.2,3.5
 *
 * 처리 단계:
 *   1) 가로로 나열된 시점 컬럼을 세로로 펼침 (unpivot)
 *   2) 산업명을 6개 표준 코드로 매핑
 *   3) 같은 지역·산업·시점에 흩어진 4개 항목을 한 행으로 재조합 (pivot)
 */
public class VacancyDataPreprocessor {

    private static final Map<String, String> INDUSTRY_NAME_MAP = new HashMap<>();
    static {
        put(INDUSTRY_NAME_MAP, "전산업", "ALL");
        put(INDUSTRY_NAME_MAP, "광업.제조업(B,C)", "BC");
        put(INDUSTRY_NAME_MAP, "건설업(F)", "F");
        put(INDUSTRY_NAME_MAP, "도소매ㆍ음식숙박업(G,I)", "GI");
        put(INDUSTRY_NAME_MAP, "사업ㆍ개인ㆍ공공서비스업(E,L~S)", "ELS");
        put(INDUSTRY_NAME_MAP, "전기ㆍ운수ㆍ통신ㆍ금융업(D,H,J,K)", "DHJK");
    }
    // 맵에 넣을 때도 NFC로 정규화 - 입력값과 항상 같은 표기 방식으로 비교되게 함
    private static void put(Map<String, String> map, String key, String value) {
        map.put(Normalizer.normalize(key, Normalizer.Form.NFC), value);
    }
    // 한글은 완성형(NFC)/조합형(NFD) 두 방식으로 저장될 수 있어, 겉보기엔 같아도
    // .equals() 비교가 실패할 수 있다. 비교 전 항상 NFC로 통일한다.
    private static String norm(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFC);
    }

    // 항목명 -> 출력 컬럼 순서 인덱스 (전체종사자, 빈일자리율, 입직률, 이직률)
    private static final Map<String, Integer> ITEM_INDEX = new HashMap<>();
    static {
        ITEM_INDEX.put(Normalizer.normalize("전체종사자", Normalizer.Form.NFC), 0);
        ITEM_INDEX.put(Normalizer.normalize("빈일자리율", Normalizer.Form.NFC), 1);
        ITEM_INDEX.put(Normalizer.normalize("입직률", Normalizer.Form.NFC), 2);
        ITEM_INDEX.put(Normalizer.normalize("이직률", Normalizer.Form.NFC), 3);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("사용법: VacancyDataPreprocessor <원본CSV> <출력CSV>");
            System.exit(1);
        }
        process(args[0], args[1]);
    }

    // 콤마를 단순 split(",")로 나누면 산업명/지역명 안에 콤마가 있는 필드가
    // 따옴표로 감싸져 있어도 잘못 쪼개진다. 따옴표 안의 콤마는 구분자로 취급하지 않는다.
    private static List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        result.add(cur.toString());
        return result;
    }

    public static void process(String inputPath, String outputPath) throws IOException {
        // (지역|산업코드|연도|반기) -> [전체종사자, 빈일자리율, 입직률, 이직률]
        Map<String, double[]> grouped = new LinkedHashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputPath), Charset.forName("CP949"))) {
            String headerLine = reader.readLine();
            String[] headers = parseCsvLine(headerLine).toArray(new String[0]);
            System.out.println("[진단] 헤더 전체(" + headers.length + "개): " + Arrays.toString(headers));
            if (headers.length > 4) {
                System.out.println("[진단] headers[4] 글자 하나씩: ");
                for (char c : headers[4].toCharArray()) System.out.print("[" + c + "]");
                System.out.println();
            }

            // 5번째 컬럼부터가 시점 컬럼 (지역별,산업별,항목,단위, 그 뒤부터)
            List<int[]> periodColumns = new ArrayList<>(); // [컬럼인덱스, 연도, 반기]
            for (int c = 4; c < headers.length; c++) {
                String h = headers[c].trim();
                if (!h.matches("\\d{4}\\..*")) continue; // "Unnamed" 등 시점이 아닌 컬럼 skip
                int year = Integer.parseInt(h.substring(0, 4));
                int half = Integer.parseInt(String.valueOf(h.charAt(h.indexOf('/') - 1)));
                periodColumns.add(new int[]{c, year, half});
            }
            System.out.println("인식된 시점 컬럼 개수: " + periodColumns.size());

            String line;
            int lineNo = 0, matchedRows = 0;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                lineNo++;
                String[] f = parseCsvLine(line).toArray(new String[0]);
                if (f.length < 4) continue;

                String region = f[0].trim();
                String industryCd = INDUSTRY_NAME_MAP.get(norm(f[1].trim()));
                Integer itemIdx = ITEM_INDEX.get(norm(f[2].trim()));

                if (lineNo <= 3) {
                    System.out.println(lineNo + "번째 줄 - 산업명:[" + f[1].trim()
                            + "] -> 매핑결과:" + industryCd + " / 항목명:[" + f[2].trim() + "] -> 매핑결과:" + itemIdx);
                }

                if (industryCd == null || itemIdx == null) continue; // 매핑 안 되는 산업/항목은 skip
                matchedRows++;

                for (int[] pc : periodColumns) {
                    int col = pc[0], year = pc[1], half = pc[2];
                    if (col >= f.length) continue;
                    String raw = f[col].trim();
                    if (raw.isEmpty()) continue; // 결측치 skip

                    double value;
                    try {
                        value = Double.parseDouble(raw);
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    String key = region + "|" + industryCd + "|" + year + "|" + half;
                    double[] values = grouped.computeIfAbsent(key, k -> {
                        double[] arr = new double[4];
                        Arrays.fill(arr, Double.NaN);
                        return arr;
                    });
                    values[itemIdx] = value;
                }
            }
            System.out.println("산업/항목 매핑에 성공한 줄 수: " + matchedRows + " / 전체 " + lineNo + "줄");
        }

        // 4개 항목이 모두 채워진 행만 출력 (하나라도 결측이면 가중평균 계산에 못 씀)
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath), Charset.forName("UTF-8"))) {
            writer.write("지역,산업코드,연도,반기,전체종사자,빈일자리율,입직률,이직률");
            writer.newLine();

            int written = 0, skipped = 0;
            for (Map.Entry<String, double[]> e : grouped.entrySet()) {
                String[] keyParts = e.getKey().split("\\|");
                double[] v = e.getValue();
                boolean complete = Arrays.stream(v).noneMatch(Double::isNaN);
                if (!complete) { skipped++; continue; }

                writer.write(String.join(",", keyParts[0], keyParts[1], keyParts[2], keyParts[3],
                        String.valueOf(v[0]), String.valueOf(v[1]), String.valueOf(v[2]), String.valueOf(v[3])));
                writer.newLine();
                written++;
            }
            System.out.println("전처리 완료: " + written + "행 출력, " + skipped + "행 결측으로 제외");
        }
    }
}