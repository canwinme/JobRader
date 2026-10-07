package com.jobradar.api.service;

import com.jobradar.api.entity.CareerJob;
import com.jobradar.api.repository.CareerJobRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository 조회 결과를 화면에 맞게 가공(직군별 그룹핑, 성장 산업별 묶음)해서 세부 직업 탐색·진로 추천 두 화면에 각각 전달하는 역할
 */
@Service
public class CareerJobService {

    private final CareerJobRepository repository;

    public CareerJobService(CareerJobRepository repository) {
        this.repository = repository;
    }

    /** 특정 산업의 직업을 직군별로 묶어서 반환한다. industryCd가 "ALL"이면 산업 구분 없이 전체를 묶음. */
    public Map<String, List<CareerJob>> getJobsByIndustryGrouped(String industryCd) {
        List<CareerJob> jobs = "ALL".equals(industryCd)
                ? repository.findAllByOrderByProfessionAscJobAsc()
                : repository.findByIndustryCdOrderByProfessionAscJobAsc(industryCd);
        Map<String, List<CareerJob>> grouped = new LinkedHashMap<>();
        for (CareerJob job : jobs) {
            grouped.computeIfAbsent(job.getProfession(), k -> new java.util.ArrayList<>()).add(job);
        }
        return grouped;
    }

    /** 적성유형(직군) 기반 추천 - 산업 구분 없이 선택한 직군의 직업 전체 반환 */
    public List<CareerJob> getJobsByProfession(String profession) {
        return repository.findByProfessionOrderByJobAsc(profession);
    }

    /** 성장 산업 연계 추천 - 주어진 산업코드 목록 각각의 대표 직업(최대 3개)을 산업별로 묶어서 반환 */
    public Map<String, List<CareerJob>> getTopJobsForIndustries(List<String> industryCodes) {
        Map<String, List<CareerJob>> result = new LinkedHashMap<>();
        for (String cd : industryCodes) {
            result.put(cd, repository.findTop3ByIndustryCdOrderByProfessionAscJobAsc(cd));
        }
        return result;
    }
}