package com.jobradar.api.repository;

import com.jobradar.api.entity.EmploymentGender;
import com.jobradar.api.entity.EmploymentGenderId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * Gender 테이블 조회. 특정 산업+특정 성별 조합의 전체 기간 추이
 */

public interface EmploymentGenderRepository extends JpaRepository<EmploymentGender, EmploymentGenderId> {

    // 특정 산업, 특정 성별의 전체 기간 추이
    List<EmploymentGender> findByIdIndustryCdAndIdGenderCdOrderByIdPeriodYearAscIdPeriodQuarterAsc(
            String industryCd, String genderCd);
}