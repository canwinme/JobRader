package com.jobradar.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 테이블 매핑  TB_BOARD_POST 매핑. 게시글 한 건(제목·내용·작성자·첨부파일)
 */
@Entity
@Table(name = "TB_BOARD_POST")
public class BoardPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String industryCd;
    private String title;

    @Lob
    private String content;

    private String author;
    private LocalDateTime createdAt;
    private String attachmentName;
    private String attachmentPath;

    protected BoardPost() {
        // JPA용 기본 생성자
    }

    public BoardPost(String industryCd, String title, String content, String author) {
        this.industryCd = industryCd;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }

    public Long getPostId() { return postId; }
    public String getIndustryCd() { return industryCd; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getAttachmentName() { return attachmentName; }
    public String getAttachmentPath() { return attachmentPath; }

    // JSTL의 fmt:formatDate는 LocalDateTime을 지원하지 않아, 화면 표시용 포맷 메소드를 따로 둠.
    public String getCreatedAtFormatted() {
        return createdAt == null ? "" : createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // 산업 코드를 화면에 보여줄 한글 이름으로 변환
    public String getIndustryNm() {
        return switch (industryCd) {
            case "ALL" -> "전산업";
            case "BC" -> "광업.제조업";
            case "F" -> "건설업";
            case "GI" -> "도소매.음식숙박업";
            case "ELS" -> "사업.개인.공공서비스업";
            case "DHJK" -> "전기.운수.통신.금융업";
            default -> industryCd;
        };
    }

    public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }
}