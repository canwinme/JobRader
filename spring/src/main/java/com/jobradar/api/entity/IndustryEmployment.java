package com.jobradar.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * 테이블 매핑 TB_INDUSTRY_EMPLOYMENT 매핑. 산업별 취업자 수 한 행
 */
@Entity
@Table(name = "TB_INDUSTRY_EMPLOYMENT")
@IdClass(IndustryEmploymentId.class)
public class IndustryEmployment {

    @Id
    private Integer periodYear;

    @Id
    private Integer periodQuarter;

    @Id
    private String industryCd;

    private String industryNm;
    private Long employedCount;

    protected IndustryEmployment() {
    }

    public Integer getPeriodYear() { return periodYear; }
    public Integer getPeriodQuarter() { return periodQuarter; }
    public String getIndustryCd() { return industryCd; }
    public String getIndustryNm() { return industryNm; }
    public Long getEmployedCount() { return employedCount; }
}