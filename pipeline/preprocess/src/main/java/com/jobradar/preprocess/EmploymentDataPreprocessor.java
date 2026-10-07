package com.jobradar.preprocess;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.*;

/**
 * REQ-002: KOSIS 성_산업별_취업자 원자료(UTF-8)를
 * EmploymentAggregationJob(Hadoop)이 읽을 수 있는 형식으로 정제함.
 *
 * 원본 구조(가로형):
 *   성별,산업별,2017.1/4,2017.2/4,...,2026.2/4
 *   계,A 농업, 임업 및 어업(01~03),1234,...
 *   계,B 광업(05~08),56,...
 *   계,C 제조업(10~34),4200,...
 *
 * 처리 단계:
 *   1) 성별 = '계' 행만 사용 (남녀 합계, 성별 구분 없이 분석하는 현재 범위 기준)
 *   2) 산업별 KSIC 개별 코드(A~U)를 6개 그룹 코드로 매핑 (A, T, U는 미분류 제외)
 *   3) 가로로 나열된 분기 컬럼을 세로로 펼침 (unpivot)
 *
 * 출력(세로형, Hadoop 입력):
 *   산업코드,연도,분기,취업자수
 *   BC,2026,2,4330
 */
public class EmploymentDataPreprocessor {

    // KSIC 산업별 컬럼값(첫 글자)이 아니라 원본 표기 자체가 코드+설명 혼합이라, 앞 글자만 추출해 매핑
    private static final Map<String, String> KSIC_TO_GROUP = new HashMap<>();
    static {
        KSIC_TO_GROUP.put("B", "BC");
        KSIC_TO_GROUP.put("C", "BC");
        KSIC_TO_GROUP.put("F", "F");
        KSIC_TO_GROUP.put("G", "GI");
        KSIC_TO_GROUP.put("I", "GI");
        for (String c : new String[]{"E", "L", "M", "N", "O", "P", "Q", "R", "S"}) {
            KSIC_TO_GROUP.put(c, "ELS");
        }
        for (String c : new String[]{"D", "H", "J", "K"}) {
            KSIC_TO_GROUP.put(c, "DHJK");
        }
        // A(농업), T, U는 6개 그룹 어디에도 속하지 않아 매핑하지 않음
    }

    // 콤마를 단순 split(",")로 나누면, 산업명 안에 콤마가 포함된 필드
    // (예: "A 농업, 임업 및 어업(01~03)")가 따옴표로 감싸져 있어도 잘못 쪼개진다.
    // 따옴표 안의 콤마는 구분자로 취급하지 않는 CSV 파서로 교체.
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

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("사용법: EmploymentDataPreprocessor <원본CSV> <출력CSV>");
            System.exit(1);
        }
        process(args[0], args[1]);
    }

    // 문자열의 실제 유니코드 코드포인트를 16진수로 보여줌
    private static String codepointsHex(String s) {
        StringBuilder sb = new StringBuilder();
        s.codePoints().forEach(cp -> sb.append(Integer.toHexString(cp)).append(" "));
        return sb.toString().trim();
    }

    public static void process(String inputPath, String outputPath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputPath), StandardCharsets.UTF_8);
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath), StandardCharsets.UTF_8)) {

            String headerLine = reader.readLine();
            String[] headers = parseCsvLine(headerLine).toArray(new String[0]);

            // 3번째 컬럼(index 2)부터 분기 컬럼
            List<int[]> periodColumns = new ArrayList<>(); // [컬럼인덱스, 연도, 분기]
            for (int c = 2; c < headers.length; c++) {
                String h = headers[c].trim();
                if (!h.matches("\\d{4}\\..*")) continue;
                int year = Integer.parseInt(h.substring(0, 4));
                int quarter = Integer.parseInt(String.valueOf(h.charAt(h.indexOf('/') - 1)));
                periodColumns.add(new int[]{c, year, quarter});
            }

            writer.write("산업코드,연도,분기,취업자수");
            writer.newLine();

            int written = 0, skipped = 0;
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                lineNo++;
                String[] f = parseCsvLine(line).toArray(new String[0]);
                if (f.length < 3) continue;

                String gender = f[0].trim();

                if (lineNo <= 3) {
                    System.out.println(lineNo + "번째 줄 - 성별 필드:[" + gender + "] "
                            + "길이:" + gender.length()
                            + " equals(\"계\"):" + gender.equals("계")
                            + " 코드포인트:" + codepointsHex(gender)
                            + " / 비교대상 \"계\" 코드포인트:" + codepointsHex("계"));
                }

                if (!gender.equals("계")) continue; // 성별 합계 행만 사용

                String industryRaw = f[1].trim();
                String firstLetter = industryRaw.isEmpty() ? "" : industryRaw.substring(0, 1);
                String groupCd = KSIC_TO_GROUP.get(firstLetter);
                if (groupCd == null) { skipped++; continue; } // A, T, U 등 미분류 skip

                for (int[] pc : periodColumns) {
                    int col = pc[0], year = pc[1], quarter = pc[2];
                    if (col >= f.length) continue;
                    String raw = f[col].trim();
                    if (raw.isEmpty()) continue;

                    long value;
                    try {
                        value = Long.parseLong(raw);
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    writer.write(groupCd + "," + year + "," + quarter + "," + value);
                    writer.newLine();
                    written++;
                }
            }
            System.out.println("전처리 완료: " + written + "행 출력, 미분류 산업 " + skipped + "행 제외");
        }
    }
}