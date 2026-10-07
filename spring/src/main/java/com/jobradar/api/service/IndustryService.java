package com.jobradar.api.service;

import com.jobradar.api.entity.IndustryEmployment;
import com.jobradar.api.entity.IndustryIndex;
import com.jobradar.api.entity.IndustryVacancy;
import com.jobradar.api.entity.EmploymentGender;
import com.jobradar.api.repository.IndustryEmploymentRepository;
import com.jobradar.api.repository.IndustryIndexRepository;
import com.jobradar.api.repository.IndustryVacancyRepository;
import com.jobradar.api.repository.EmploymentGenderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 세 Repository(Vacancy/Employment/Index)를 조합해 화면·API에 필요한 형태로 데이터를 제공.
 * Controller가 요청하는 화면·API 형태(순위/시각화/추이/성별)로 조립해서 반환
 */
@Service
public class IndustryService {

    private final IndustryVacancyRepository vacancyRepository;
    private final IndustryEmploymentRepository employmentRepository;
    private final IndustryIndexRepository indexRepository;
    private final EmploymentGenderRepository genderRepository;

    // 생성자를 통한 의존성 주입 - Spring이 자동으로 Repository 구현체를 넣어줌
    public IndustryService(IndustryVacancyRepository vacancyRepository,
                            IndustryEmploymentRepository employmentRepository,
                            IndustryIndexRepository indexRepository,
                            EmploymentGenderRepository genderRepository) {
        this.vacancyRepository = vacancyRepository;
        this.employmentRepository = employmentRepository;
        this.indexRepository = indexRepository;
        this.genderRepository = genderRepository;
    }

    /** 최신 시점 기준 산업별 순위  */
    public List<IndustryIndex> getRanking() {
        return indexRepository.findByIndRankIsNotNullOrderByIndRankAsc();
    }

    /** 특정 시점(연도, 반기)의 산업별 구인활력 시각화 */
    public List<IndustryVacancy> getVacancySnapshot(Integer year, Integer half) {
        return vacancyRepository.findByPeriodYearAndPeriodHalf(year, half);
    }

    /** 특정 시점(연도, 분기)의 산업별 취업자 시각화 */
    public List<IndustryEmployment> getEmploymentSnapshot(Integer year, Integer quarter) {
        return employmentRepository.findByPeriodYearAndPeriodQuarter(year, quarter);
    }

    /** 특정 산업 하나의 구인활력 전체 기간 추이  */
    public List<IndustryVacancy> getVacancyTrend(String industryCd) {
        return vacancyRepository.findByIndustryCdOrderByPeriodYearAscPeriodHalfAsc(industryCd);
    }

    /** 특정 산업 하나의 취업자·증감률 전체 기간 추이  */
    public List<IndustryIndex> getGrowthTrend(String industryCd) {
        return indexRepository.findByIdIndustryCdOrderByIdPeriodYearAscIdPeriodQuarterAsc(industryCd);
    }

    /** 특정 산업의 성별(전체/남자/여자) 취업자 추이   */
    public List<EmploymentGender> getEmploymentByGender(String industryCd, String genderCd) {
        return genderRepository.findByIdIndustryCdAndIdGenderCdOrderByIdPeriodYearAscIdPeriodQuarterAsc(industryCd, genderCd);
    }
}