package com.jobradar.api.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 *  복합키 표현 Gender의 복합키(연도+분기+산업코드+성별코드).
 */
@Embeddable
public class EmploymentGenderId implements Serializable {

    private Integer periodYear;
    private Integer periodQuarter;
    private String industryCd;
    private String genderCd;

    public EmploymentGenderId() {
    }

    public EmploymentGenderId(Integer periodYear, Integer periodQuarter, String industryCd, String genderCd) {
        this.periodYear = periodYear;
        this.periodQuarter = periodQuarter;
        this.industryCd = industryCd;
        this.genderCd = genderCd;
    }

    public Integer getPeriodYear() { return periodYear; }
    public Integer getPeriodQuarter() { return periodQuarter; }
    public String getIndustryCd() { return industryCd; }
    public String getGenderCd() { return genderCd; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmploymentGenderId)) return false;
        EmploymentGenderId that = (EmploymentGenderId) o;
        return Objects.equals(periodYear, that.periodYear)
                && Objects.equals(periodQuarter, that.periodQuarter)
                && Objects.equals(industryCd, that.industryCd)
                && Objects.equals(genderCd, that.genderCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodYear, periodQuarter, industryCd, genderCd);
    }
}