package com.jobradar.api.batch;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 커리어넷 JOB(목록) API 응답(JSON)에서, 확정된 42개 직군->6개 산업 매핑표를 기준
 *
 * 출력 CSV는 이후 JOB_VIEW(상세조회) 호출의 입력 목록(jobdicSeq)으로 사용함.
 *
 */
public class CareerNetJobSelector {

    // 42개 직군(profession) -> 6개 산업 매핑
    private static final Map<String, String> PROFESSION_TO_INDUSTRY = new LinkedHashMap<>();
    static {
        put("F", "기능직", "안전 관련직");
        put("BC", "공학 전문직", "공학 기술직", "의복제조 관련직", "악기 관련직");
        put("GI", "조리 관련직", "일반 서비스직", "영업관련 서비스직");
        put("DHJK", "IT관련전문직", "웹·게임·애니메이션 관련직", "영상 관련직",
                "일반운전 관련직", "금융 및 경영 관련직", "고급 운전 관련직");
        put("ELS", "법률 및 사회활동 관련직", "예술기획 관련직", "환경관련 전문직", "기타 특수 예술직",
                "디자인 관련직", "이공계 교육 관련직", "인문계 교육 관련직", "언어 관련 전문직",
                "인문 및 사회과학 관련직", "의료관련 전문직", "운동 관련직", "무용 관련직", "음악 관련직",
                "연기 관련직", "보건의료 관련 서비스직", "이학 전문직", "사회서비스직", "교육관련 서비스직",
                "기획서비스직", "사무 관련직", "회계 관련직", "매니지먼트 관련직", "기타 게임·오락·스포츠 관련직",
                "이미용 관련직", "작가 관련직", "미술 및 공예 관련직", "자연친화 관련직");
        
    }

    private static void put(String industryCd, String... professions) {
        for (String p : professions) PROFESSION_TO_INDUSTRY.put(p, industryCd);
    }

    static class JobRow {
        String jobdicSeq, job, aptdTypeCode, profession, possibility;
        String industry;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("사용법: CareerNetJobSelector <커리어넷목록JSON경로> <출력CSV경로>");
            System.exit(1);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(Files.readString(Path.of(args[0]), StandardCharsets.UTF_8));
        JsonNode content = root.path("dataSearch").path("content");

        List<JobRow> allJobs = new ArrayList<>();
        for (JsonNode node : content) {
            JobRow row = new JobRow();
            row.jobdicSeq = node.path("jobdicSeq").asText("");
            row.job = node.path("job").asText("");
            row.aptdTypeCode = node.path("aptd_type_code").asText("");
            row.profession = node.path("profession").asText("");
            row.possibility = node.path("possibility").asText("");
            if (!row.jobdicSeq.isBlank()) allJobs.add(row);
        }
        System.out.println("[진단] 목록 전체 파싱 건수: " + allJobs.size());

        // 매핑표에 있는 직군(profession)에 해당하는 건 전부 포함 
        // profession 값이  화면에서 소그룹 제목으로 쓰임
        List<JobRow> selected = new ArrayList<>();
        Map<String, Integer> countByIndustry = new LinkedHashMap<>();
        for (JobRow row : allJobs) {
            String industry = PROFESSION_TO_INDUSTRY.get(row.profession);
            if (industry == null) continue; // 매핑표에 없는 직군은 제외
            row.industry = industry;
            selected.add(row);
            countByIndustry.merge(industry, 1, Integer::sum);
        }
        countByIndustry.forEach((cd, cnt) -> System.out.println("[진단] " + cd + ": " + cnt + "개 선정"));

        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(args[1]), StandardCharsets.UTF_8)) {
            writer.write("jobdicSeq,job,aptd_type_code,profession,industry");
            writer.newLine();
            for (JobRow row : selected) {
               //CSV로 쓸 때는 여전히 콤마 포함 값에 따옴표를 씌워야 함 (aptd_type_code에 "104729,104729"처럼 콤마가 있음)
                writer.write(String.join(",",
                        row.jobdicSeq,
                        csvEscape(row.job),
                        csvEscape(row.aptdTypeCode),
                        csvEscape(row.profession),
                        row.industry));
                writer.newLine();
            }
        }
        System.out.println("총 " + selected.size() + "건 -> " + args[1] + " 저장 완료");
    }

    private static String csvEscape(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}