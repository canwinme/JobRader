package com.jobradar.api.repository;

import com.jobradar.api.entity.CareerJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 산업별, 전체, 직군별, 성장산업 상위 3개 등 4가지 조건으로 CareerJob을 조회하는 쿼리 메소드 모음
 */
public interface CareerJobRepository extends JpaRepository<CareerJob, String> {

    // 특정 산업의 직업을 직군(profession) 순으로 정렬해서 조회 - 같은 직군끼리 묶여서 나옴
    List<CareerJob> findByIndustryCdOrderByProfessionAscJobAsc(String industryCd);

    // 전산업(ALL) 선택 시 - 산업 구분 없이 전체를 직군 순으로 조회
    List<CareerJob> findAllByOrderByProfessionAscJobAsc();

    // 적성유형(직군) 기반 추천용 - 산업 구분 없이 특정 직군의 직업만 조회
    List<CareerJob> findByProfessionOrderByJobAsc(String profession);

    // 성장 산업 연계 추천용 - 특정 산업의 직업을 정해진 개수만 조회 (상위 3개)
    List<CareerJob> findTop3ByIndustryCdOrderByProfessionAscJobAsc(String industryCd);
}