package com.jobradar.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
/**
 * 테이블 매핑, 직업 하나(직업명·직군·자격증·훈련·교육과정)를 표현하는 Entity
 */
@Entity
@Table(name = "TB_CAREER_JOB")
public class CareerJob {

    @Id
    private String jobdicSeq;

    private String job;
    private String aptdTypeCode;
    private String profession;
    private String industryCd;

    @Lob
    private String certification;
    @Lob
    private String training;
    @Lob
    private String preparation;

    protected CareerJob() {
        // JPA용 기본 생성자
    }

    public String getJobdicSeq() { return jobdicSeq; }
    public String getJob() { return job; }
    public String getAptdTypeCode() { return aptdTypeCode; }
    public String getProfession() { return profession; }
    public String getIndustryCd() { return industryCd; }
    public String getCertification() { return certification; }
    public String getTraining() { return training; }
    public String getPreparation() { return preparation; }
}