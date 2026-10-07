package com.jobradar.api.entity;

import java.io.Serializable;
import java.util.Objects;
/**
 * 복합키 표현 Employment의 복합키(연도+분기+산업코드).
 */
public class IndustryEmploymentId implements Serializable {

    private Integer periodYear;
    private Integer periodQuarter;
    private String industryCd;

    public IndustryEmploymentId() {
    }

    public IndustryEmploymentId(Integer periodYear, Integer periodQuarter, String industryCd) {
        this.periodYear = periodYear;
        this.periodQuarter = periodQuarter;
        this.industryCd = industryCd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndustryEmploymentId)) return false;
        IndustryEmploymentId that = (IndustryEmploymentId) o;
        return Objects.equals(periodYear, that.periodYear)
                && Objects.equals(periodQuarter, that.periodQuarter)
                && Objects.equals(industryCd, that.industryCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodYear, periodQuarter, industryCd);
    }

    public Integer getPeriodYear() { return periodYear; }
    public Integer getPeriodQuarter() { return periodQuarter; }
    public String getIndustryCd() { return industryCd; }
}