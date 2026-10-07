package com.jobradar.api.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * 테이블 매핑 TB_INDUSTRY_INDEX 매핑. 산업별 취업자 증감률·순위 한 행
 */
@Entity
@Table(name = "TB_INDUSTRY_INDEX")
public class IndustryIndex {

    @EmbeddedId
    private IndustryIndexId id;

    private String industryNm;
    private Long employedCount;
    private Double growthRate;
    private Integer indRank;

    protected IndustryIndex() {
    }

    // 아래 3개는 id 안에 있는 값을 그대로 꺼내주는 편의 메소드.
    // Controller/Service 쪽 코드는 그대로 getPeriodYear() 등을 써도 되게 이름을 유지함.
    public Integer getPeriodYear() { return id.getPeriodYear(); }
    public Integer getPeriodQuarter() { return id.getPeriodQuarter(); }
    public String getIndustryCd() { return id.getIndustryCd(); }

    public IndustryIndexId getId() { return id; }
    public String getIndustryNm() { return industryNm; }
    public Long getEmployedCount() { return employedCount; }
    public Double getGrowthRate() { return growthRate; }
    public Integer getIndRank() { return indRank; }
}