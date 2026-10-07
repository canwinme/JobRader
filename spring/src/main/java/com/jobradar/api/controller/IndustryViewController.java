package com.jobradar.api.controller;

import com.jobradar.api.service.IndustryService;
import com.jobradar.api.service.CareerJobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 홈부터 진로 추천까지, 총 7개 화면(홈/순위/그래프/향후확장 2종/세부직업탐색/산업분류상세/진로추천)의 요청을 받아 
 * Service에서 데이터를 가져온 뒤 어떤 JSP를 보여줄지 결정하는 화면 라우팅 담당 클래스 
 */
@Controller
@RequestMapping("/view")
public class IndustryViewController {

    private final IndustryService industryService;
    private final CareerJobService careerJobService;

    public IndustryViewController(IndustryService industryService, CareerJobService careerJobService) {
        this.industryService = industryService;
        this.careerJobService = careerJobService;
    }

    // GET http://localhost:8080/view/
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // GET http://localhost:8080/view/ranking
    @GetMapping("/ranking")
    public String rankingPage(Model model) {
        // Model에 담은 값은 JSP에서 ${rankings} 형태로 그대로 꺼내 쓸 수 있다.
        model.addAttribute("rankings", industryService.getRanking());
        return "ranking";
    }

    // GET http://localhost:8080/view/trend/BC
    // 실제 데이터는 이 화면의 JS가 기존 REST API(/api/industries/{code}/growth-trend)를
    // 직접 호출해서 그리므로, 여기서는 화면에 industryCd만 전달해주면 된다.
    @GetMapping("/trend/{industryCd}")
    public String trendPage(@PathVariable String industryCd, Model model) {
        model.addAttribute("industryCd", industryCd);
        return "trend";
    }

    // GET http://localhost:8080/view/expansion
    // 더미 값이지만 실제 관찰된 추세(건설업 고이직률, 사업서비스업 고성장 등)를 반영해 구상함.
    @GetMapping("/expansion")
    public String expansionPage(Model model) {
        List<Map<String, Object>> dummyCompetition = new ArrayList<>();
        // 건설업: 실제 이직률 18.2%로 압도적 1위(기피현상) -> 가상 구직자 적고 채용수요 많음 -> 경쟁도 낮음(기회 많음)
        dummyCompetition.add(Map.of("industryNm", "건설업", "jobSeekers", 3200, "vacancyDemand", 8500, "competitionIndex", 0.38));
        // 사업.개인.공공서비스업: 실제 취업자 증감률 1위(+3.67%, 성장세) -> 인기 산업 가정 -> 경쟁도 높음
        dummyCompetition.add(Map.of("industryNm", "사업.개인.공공서비스업", "jobSeekers", 15400, "vacancyDemand", 9800, "competitionIndex", 1.57));
        // 전기.운수.통신.금융업: 금융 등 인기 직종 포함 가정 -> 경쟁도 높음
        dummyCompetition.add(Map.of("industryNm", "전기.운수.통신.금융업", "jobSeekers", 9100, "vacancyDemand", 6200, "competitionIndex", 1.47));
        // 도소매.음식숙박업: 진입장벽 낮아 구직자 쏠림 가정 -> 경쟁도 다소 높음
        dummyCompetition.add(Map.of("industryNm", "도소매.음식숙박업", "jobSeekers", 11200, "vacancyDemand", 7300, "competitionIndex", 1.53));
        // 광업.제조업: 실제 취업자 유일하게 감소(-1.12%)하지만 전통적 안정 이미지 가정 -> 중간 수준 경쟁도
        dummyCompetition.add(Map.of("industryNm", "광업.제조업", "jobSeekers", 8300, "vacancyDemand", 6900, "competitionIndex", 1.20));
        model.addAttribute("dummyCompetition", dummyCompetition);
        return "expansion";
    }
    
    // GET http://localhost:8080/view/expansion/youth-senior
    // 향후 확장 방안 	2. 청년 고령 맞춤 지원
    @GetMapping("/expansion/youth-senior")
    public String expansionYouthSeniorPage() {
        return "expansion-youthsenior";
    }

    // GET http://localhost:8080/view/jobsearch?industryCd=BC
    // 세부 직업 탐색. 커리어넷 API 실제 데이터 기반이라 향후 확장(미리보기)이 아니라
    // 최상위 메뉴로 분리한다.
    @GetMapping("/jobsearch")
    public String jobSearchPage(
            @RequestParam(required = false, defaultValue = "F") String industryCd, Model model) {
        model.addAttribute("selectedIndustry", industryCd);
        model.addAttribute("groupedJobs", careerJobService.getJobsByIndustryGrouped(industryCd));
        return "jobsearch";
    }

    // GET http://localhost:8080/view/industry-detail
    // 6개 대분류 안에 실제로 어떤 세부 산업(KSIC)이 포함되는지 보여준다.
    @GetMapping("/industry-detail")
    public String industryDetailPage(Model model) {
        List<Map<String, Object>> details = new ArrayList<>();
        List<String> bcList = List.of("B 광업(05~08)", "C 제조업(10~34)");
        List<String> fList = List.of("F 건설업(41~42)");
        List<String> giList = List.of("G 도매 및 소매업(45~47)", "I 숙박 및 음식점업(55~56)");
        List<String> elsList = List.of(
                "E 수도, 하수 및 폐기물 처리, 원료 재생업(36~39)",
                "L 부동산업(68)",
                "M 전문, 과학 및 기술 서비스업(70~73)",
                "N 사업시설 관리, 사업 지원 및 임대 서비스업(74~76)",
                "O 공공행정, 국방 및 사회보장 행정(84)",
                "P 교육 서비스업(85)",
                "Q 보건업 및 사회복지 서비스업(86~87)",
                "R 예술, 스포츠 및 여가관련 서비스업(90~91)",
                "S 협회 및 단체, 수리 및 기타 개인 서비스업(94~96)");
        List<String> dhjkList = List.of(
                "D 전기, 가스, 증기 및 공기조절 공급업(35)",
                "H 운수 및 창고업(49~52)",
                "J 정보통신업(58~63)",
                "K 금융 및 보험업(64~66)");

        // 전산업 = 아래 5개 대분류를 합친 것 (농업은 애초에 6개 분류 대상이 아니라 제외됨)
        List<String> allList = new ArrayList<>();
        allList.addAll(bcList); allList.addAll(fList); allList.addAll(giList);
        allList.addAll(elsList); allList.addAll(dhjkList);
        details.add(Map.of("industryCd", "ALL", "industryNm", "전산업", "subIndustries", allList));

        details.add(Map.of("industryCd", "BC", "industryNm", "광업.제조업", "subIndustries", bcList));
        details.add(Map.of("industryCd", "F", "industryNm", "건설업", "subIndustries", fList));
        details.add(Map.of("industryCd", "GI", "industryNm", "도소매.음식숙박업", "subIndustries", giList));
        details.add(Map.of("industryCd", "ELS", "industryNm", "사업.개인.공공서비스업", "subIndustries", elsList));
        details.add(Map.of("industryCd", "DHJK", "industryNm", "전기.운수.통신.금융업", "subIndustries", dhjkList));
        model.addAttribute("details", details);
        return "industry-detail";
    }

    // 42개 직군(=적성유형) 목록 - CareerNetJobSelector의 매핑표와 동일하게 유지
    private static final List<String> PROFESSION_LIST = List.of(
            "기능직", "안전 관련직",
            "공학 전문직", "공학 기술직", "의복제조 관련직", "악기 관련직",
            "조리 관련직", "일반 서비스직", "영업관련 서비스직",
            "IT관련전문직", "웹·게임·애니메이션 관련직", "영상 관련직", "일반운전 관련직", "금융 및 경영 관련직", "고급 운전 관련직",
            "법률 및 사회활동 관련직", "예술기획 관련직", "환경관련 전문직", "기타 특수 예술직",
            "디자인 관련직", "이공계 교육 관련직", "인문계 교육 관련직", "언어 관련 전문직",
            "인문 및 사회과학 관련직", "의료관련 전문직", "운동 관련직", "무용 관련직", "음악 관련직",
            "연기 관련직", "보건의료 관련 서비스직", "이학 전문직", "사회서비스직", "교육관련 서비스직",
            "기획서비스직", "사무 관련직", "회계 관련직", "매니지먼트 관련직", "기타 게임·오락·스포츠 관련직",
            "이미용 관련직", "작가 관련직", "미술 및 공예 관련직", "자연친화 관련직"
    );

    // GET http://localhost:8080/view/recommend?profession=IT관련전문직
    // 진로 추천.
    @GetMapping("/recommend")
    public String recommendPage(@RequestParam(required = false) String profession, Model model) {
        // ① 성장 산업 연계 - 순위 화면과 동일한 데이터에서 상위 3개만 사용
        List<com.jobradar.api.entity.IndustryIndex> topIndustries = industryService.getRanking();
        if (topIndustries.size() > 3) topIndustries = topIndustries.subList(0, 3);
        List<String> topCodes = topIndustries.stream()
                .map(com.jobradar.api.entity.IndustryIndex::getIndustryCd)
                .toList();
        model.addAttribute("topIndustries", topIndustries);
        model.addAttribute("growthJobs", careerJobService.getTopJobsForIndustries(topCodes));

        // ② 적성유형(직군) 기반 - 사용자가 선택했을 때만 조회
        model.addAttribute("professionList", PROFESSION_LIST);
        if (profession != null && !profession.isBlank()) {
            model.addAttribute("selectedProfession", profession);
            model.addAttribute("aptitudeJobs", careerJobService.getJobsByProfession(profession));
        }
        return "recommend";
    }
}