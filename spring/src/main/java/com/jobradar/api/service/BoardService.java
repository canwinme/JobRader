package com.jobradar.api.service;

import com.jobradar.api.entity.BoardPost;
import com.jobradar.api.repository.BoardPostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 게시글 CRUD 담당 — 글 조회·목록뿐 아니라 파일을 실제로 서버 폴더에 저장·삭제하는 로직까지 포함
 */
@Service
public class BoardService {

    private final String uploadDir;
    private final BoardPostRepository repository;

    public BoardService(BoardPostRepository repository,
                        @Value("${jobradar.upload.dir:./uploads}") String uploadDir) {
        this.repository = repository;
        this.uploadDir = uploadDir;
    }

    public List<BoardPost> getList(String industryCd) {
        if (industryCd == null || industryCd.isBlank()) {
            return repository.findAllByOrderByCreatedAtDesc();
        }
        return repository.findByIndustryCdOrderByCreatedAtDesc(industryCd);
    }

    public BoardPost getPost(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다: " + id));
    }

    public void createPost(String industryCd, String title, String content, String author, MultipartFile file) throws IOException {
        BoardPost post = new BoardPost(industryCd, title, content, author);

        if (file != null && !file.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath); // 폴더 없으면 자동 생성

            // 파일명이 겹치지 않도록 타임스탬프를 붙여서 저장
            String storedName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path target = uploadPath.resolve(storedName);
            file.transferTo(target.toFile());

            post.setAttachmentName(file.getOriginalFilename()); // 화면에 보여줄 원래 파일명
            post.setAttachmentPath(target.toString());          // 실제 저장 경로
        }

        repository.save(post);
    }

    public void deletePost(Long id) throws IOException {
        BoardPost post = getPost(id);
        if (post.getAttachmentPath() != null) {
            Files.deleteIfExists(Paths.get(post.getAttachmentPath())); // 첨부파일도 같이 삭제
        }
        repository.deleteById(id);
    }
}