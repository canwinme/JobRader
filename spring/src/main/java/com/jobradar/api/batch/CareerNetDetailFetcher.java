package com.jobradar.api.batch;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * CareerNetJobSelector가 만든 목록(jobdicSeq 포함)을 읽어, 각 jobdicSeq마다
 * JOB_VIEW(상세조회) API를 호출해 관련자격증/직업훈련/정규교육과정을 채운 최종 CSV를 만듬.
 *
 */
public class CareerNetDetailFetcher {

    private static final String BASE_URL = "https://www.career.go.kr/cnet/openapi/getOpenApi";

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("사용법: CareerNetDetailFetcher <입력CSV> <출력CSV> <API키>");
            System.exit(1);
        }
        String inputCsv = args[0], outputCsv = args[1], apiKey = args[2];

        ObjectMapper mapper = new ObjectMapper();
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

        List<String[]> rows = readCsv(inputCsv); // [jobdicSeq, job, aptd_type_code, profession, industry]

        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(outputCsv), StandardCharsets.UTF_8)) {
            writer.write("jobdicSeq,job,aptd_type_code,profession,industry,관련자격증,직업훈련,정규교육과정");
            writer.newLine();

            for (String[] row : rows) {
                String jobdicSeq = row[0];
                String query = String.join("&",
                        "apiKey=" + apiKey,
                        "svcType=api",
                        "svcCode=JOB_VIEW",
                        "gubun=job_dic_list",
                        "contentType=json",
                        "jobdicSeq=" + jobdicSeq
                );
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "?" + query))
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                String certification = "", training = "", preparation = "";
                try {
                    if (response.statusCode() != 200) {
                        System.out.println("경고 :" + row[1] + "(jobdicSeq=" + jobdicSeq + ") HTTP " + response.statusCode()
                                + " - 응답 앞부분: " + safeSubstring(response.body(), 300));
                    } else {
                        JsonNode root = mapper.readTree(response.body());
                        JsonNode contentArr = root.path("dataSearch").path("content");
                        if (contentArr.isArray() && contentArr.size() > 0) {
                            JsonNode detail = contentArr.get(0);
                            for (JsonNode item : detail.path("prepareway")) {
                                if (item.has("preparation")) preparation = sanitize(item.path("preparation").asText(""));
                                if (item.has("training")) training = sanitize(item.path("training").asText(""));
                                if (item.has("certification")) certification = sanitize(item.path("certification").asText(""));
                            }
                        } else {
                            System.out.println("경고 : " + row[1] + "(jobdicSeq=" + jobdicSeq + ") content 비어있음 - 응답 앞부분: "
                                    + safeSubstring(response.body(), 300));
                        }
                    }
                } catch (Exception parseEx) {
                    // JSON이 아니라 HTML(에러 페이지) 등이 온 경우 - 원인 파악을 위해 응답 원문을 출력하고
                    // 이 건은 빈 값으로 남긴 채 다음 직업으로 계속 진행 (여기서 멈추지 않음)
                    System.out.println("오류 :  " + row[1] + "(jobdicSeq=" + jobdicSeq + ") 파싱 실패: " + parseEx.getMessage());
                    System.out.println("       응답 앞부분: " + safeSubstring(response.body(), 300));
                }

                writer.write(String.join(",",
                        row[0], csvEscape(row[1]), csvEscape(row[2]), csvEscape(row[3]), row[4],
                        csvEscape(certification), csvEscape(training), csvEscape(preparation)));
                writer.newLine();
                writer.flush();

                System.out.println(row[1] + "(jobdicSeq=" + jobdicSeq + ") 처리 완료");
                Thread.sleep(200); // 공공 API에 짧은 시간에 너무 많은 요청을 보내지 않도록 최소한의 간격을 둠
            }
        }
        System.out.println("완료: " + outputCsv);
    }

    // 설명 텍스트 안의 실제 줄바꿈을 공백으로 치환 
    // CSV 처리 로직에서 한 항목이 여러 줄로 쪼개져 읽히는 문제가 생기기 때문
    private static String sanitize(String text) {
        return text.replace("\r\n", " ").replace("\n", " ").replace("\r", " ").trim();
    }

    private static String safeSubstring(String s, int len) {
        if (s == null) return "";
        return s.substring(0, Math.min(len, s.length()));
    }

    private static String csvEscape(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    // CareerNetJobSelector가 만든 CSV를 다시 읽기 위한 간단한 파서
    private static List<String[]> readCsv(String path) throws Exception {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
            reader.readLine(); // 헤더 skip
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                rows.add(parseCsvLine(line));
            }
        }
        return rows;
    }

    private static String[] parseCsvLine(String line) {
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
        return result.toArray(new String[0]);
    }
}