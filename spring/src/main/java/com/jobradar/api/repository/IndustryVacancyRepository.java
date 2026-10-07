package com.jobradar.api.repository;

import com.jobradar.api.entity.IndustryVacancy;
import com.jobradar.api.entity.IndustryVacancyId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 	DB 조회 Vacancy 테이블 조회. 특정 시점 전체 조회, 특정 산업의 전체 기간 추이 조회
 */
public interface IndustryVacancyRepository extends JpaRepository<IndustryVacancy, IndustryVacancyId> {

    // 메소드 이름만으로 "WHERE PERIOD_YEAR=? AND PERIOD_HALF=?" 쿼리가 자동 생성됨 (기간)
    List<IndustryVacancy> findByPeriodYearAndPeriodHalf(Integer periodYear, Integer periodHalf);

    // 특정 산업의 전체 기간 추이 조회 (오래된 시점 순 정렬)
    List<IndustryVacancy> findByIndustryCdOrderByPeriodYearAscPeriodHalfAsc(String industryCd);
}