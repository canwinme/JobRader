package com.jobradar.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * 테이블 매핑 TB_INDUSTRY_VACANCY 매핑. 산업별 구인활력(빈일자리율·입직률·이직률) 한 행
 */
@Entity
@Table(name = "TB_INDUSTRY_VACANCY")
@IdClass(IndustryVacancyId.class)
public class IndustryVacancy {

    @Id
    private Integer periodYear;

    @Id
    private Integer periodHalf;

    @Id
    private String industryCd;

    private String industryNm;
    private Long totalWorkers;
    private Double vacancyRate;
    private Double hireRate;
    private Double separationRate;

    protected IndustryVacancy() {
        // JPA는 매개변수 없는 생성자가 반드시 필요함 (protected면 충분)
    }

    
    public Integer getPeriodYear() { return periodYear; }
    public Integer getPeriodHalf() { return periodHalf; }
    public String getIndustryCd() { return industryCd; }
    public String getIndustryNm() { return industryNm; }
    public Long getTotalWorkers() { return totalWorkers; }
    public Double getVacancyRate() { return vacancyRate; }
    public Double getHireRate() { return hireRate; }
    public Double getSeparationRate() { return separationRate; }
}