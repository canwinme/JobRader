package com.jobradar.api.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * TB_INDUSTRY_VACANCY의 복합키(PERIOD_YEAR, PERIOD_HALF, INDUSTRY_CD)를 표현하는 클래스.
 */
public class IndustryVacancyId implements Serializable {

    private Integer periodYear;
    private Integer periodHalf;
    private String industryCd;

    public IndustryVacancyId() {
    }

    public IndustryVacancyId(Integer periodYear, Integer periodHalf, String industryCd) {
        this.periodYear = periodYear;
        this.periodHalf = periodHalf;
        this.industryCd = industryCd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndustryVacancyId)) return false;
        IndustryVacancyId that = (IndustryVacancyId) o;
        return Objects.equals(periodYear, that.periodYear)
                && Objects.equals(periodHalf, that.periodHalf)
                && Objects.equals(industryCd, that.industryCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodYear, periodHalf, industryCd);
    }

    public Integer getPeriodYear() { return periodYear; }
    public Integer getPeriodHalf() { return periodHalf; }
    public String getIndustryCd() { return industryCd; }
}