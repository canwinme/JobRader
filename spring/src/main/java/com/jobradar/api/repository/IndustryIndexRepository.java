package com.jobradar.api.repository;

import com.jobradar.api.entity.IndustryIndex;
import com.jobradar.api.entity.IndustryIndexId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *  DB 조회 	Index 테이블 조회. 순위 화면용(최신 시점만), 특정 산업 증감률 추이용
 */
public interface IndustryIndexRepository extends JpaRepository<IndustryIndex, IndustryIndexId> {

    // IND_RANK가 채워진 행만(=최신 시점) 순위 오름차순으로 조회 
    List<IndustryIndex> findByIndRankIsNotNullOrderByIndRankAsc();

    // industryCd가 이제 id 안에 있어서 Id를 거쳐 조회 
    List<IndustryIndex> findByIdIndustryCdOrderByIdPeriodYearAscIdPeriodQuarterAsc(String industryCd);
}