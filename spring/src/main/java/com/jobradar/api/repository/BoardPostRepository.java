package com.jobradar.api.repository;

import com.jobradar.api.entity.BoardPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * DB 조회 게시글 조회. 산업 필터별 최신순, 전체 최신순
 */
public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

    // 특정 산업 게시글만 최신순으로
    List<BoardPost> findByIndustryCdOrderByCreatedAtDesc(String industryCd);

    // 전체 게시글 최신순 (산업 필터 없이 볼 때)
    List<BoardPost> findAllByOrderByCreatedAtDesc();
}