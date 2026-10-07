package com.jobradar.api.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 *  테이블 매핑 TB_EMPLOYMENT_GENDER 매핑. 산업×성별 취업자 수 한 행
 */
@Entity
@Table(name = "TB_EMPLOYMENT_GENDER")
public class EmploymentGender {

    @EmbeddedId
    private EmploymentGenderId id;

    private String industryNm;
    private Long employedCount;

    protected EmploymentGender() {
    }

    public Integer getPeriodYear() { return id.getPeriodYear(); }
    public Integer getPeriodQuarter() { return id.getPeriodQuarter(); }
    public String getIndustryCd() { return id.getIndustryCd(); }
    public String getGenderCd() { return id.getGenderCd(); }
    public String getIndustryNm() { return industryNm; }
    public Long getEmployedCount() { return employedCount; }
}