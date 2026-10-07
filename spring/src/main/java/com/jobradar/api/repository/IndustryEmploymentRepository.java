package com.jobradar.api.repository;

import com.jobradar.api.entity.IndustryEmployment;
import com.jobradar.api.entity.IndustryEmploymentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 	DB조회	Employment 테이블 조회. 특정 시점 전체 조회, 특정 산업의 전체 기간 추이 조회
 */
public interface IndustryEmploymentRepository extends JpaRepository<IndustryEmployment, IndustryEmploymentId> {
	 // 특정 연도·분기의 6개 산업 취업자 수를 한 번에 조회 (특정 시점 확인용)
    List<IndustryEmployment> findByPeriodYearAndPeriodQuarter(Integer periodYear, Integer periodQuarter);
     // 특정 산업 하나의 전체 기간(2017~2026) 취업자 수 변화를 시간순으로 조회 (추이 그래프)
    List<IndustryEmployment> findByIndustryCdOrderByPeriodYearAscPeriodQuarterAsc(String industryCd);
}