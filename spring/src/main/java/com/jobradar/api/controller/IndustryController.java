package com.jobradar.api.controller;

import com.jobradar.api.entity.IndustryEmployment;
import com.jobradar.api.entity.IndustryIndex;
import com.jobradar.api.entity.IndustryVacancy;
import com.jobradar.api.entity.EmploymentGender;
import com.jobradar.api.service.IndustryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 산업별 분석결과 조회 REST API.
 * @RestController가 붙으면 이 클래스의 메소드 리턴값이 자동으로 JSON으로 변환되어 응답.
 * 	REST API 제공. 순위·구인활력·취업자·추이·성별 데이터를 JSON으로 반환.
 */
@RestController
@RequestMapping("/api/industries")
public class IndustryController {

    private final IndustryService industryService;

    public IndustryController(IndustryService industryService) {
        this.industryService = industryService;
    }

    // GET http://localhost:8080/api/industries/ranking
    // 최신 시점 기준 산업별 취업자 증감률 순위
    @GetMapping("/ranking")
    public List<IndustryIndex> getRanking() {
        return industryService.getRanking();
    }

    // GET http://localhost:8080/api/industries/vacancy?year=2025&half=2
    // 특정 시점의 산업별 구인활력(빈일자리율·입직률·이직률)
    @GetMapping("/vacancy")
    public List<IndustryVacancy> getVacancySnapshot(
            @RequestParam Integer year,
            @RequestParam Integer half) {
        return industryService.getVacancySnapshot(year, half);
    }

    // GET http://localhost:8080/api/industries/employment?year=2026&quarter=2
    // 특정 시점의 산업별 취업자 수
    @GetMapping("/employment")
    public List<IndustryEmployment> getEmploymentSnapshot(
            @RequestParam Integer year,
            @RequestParam Integer quarter) {
        return industryService.getEmploymentSnapshot(year, quarter);
    }

    // GET http://localhost:8080/api/industries/BC/vacancy-trend
    // 특정 산업의 구인활력 전체 기간 추이
    @GetMapping("/{industryCd}/vacancy-trend")
    public List<IndustryVacancy> getVacancyTrend(@PathVariable String industryCd) {
        return industryService.getVacancyTrend(industryCd);
    }

    // GET http://localhost:8080/api/industries/BC/growth-trend
    // 특정 산업의 취업자·증감률 전체 기간 추이
    @GetMapping("/{industryCd}/growth-trend")
    public List<IndustryIndex> getGrowthTrend(@PathVariable String industryCd) {
        return industryService.getGrowthTrend(industryCd);
    }

    // GET http://localhost:8080/api/industries/BC/employment-by-gender?gender=M
    // 특정 산업의 성별 취업자 추이
    @GetMapping("/{industryCd}/employment-by-gender")
    public List<EmploymentGender> getEmploymentByGender(
            @PathVariable String industryCd,
            @RequestParam(defaultValue = "ALL") String gender) {
        return industryService.getEmploymentByGender(industryCd, gender);
    }
}