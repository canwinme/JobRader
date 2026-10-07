package com.jobradar.api.controller;

import com.jobradar.api.service.BoardService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 산업별 게시판. 목록(전체/필터) - 글쓰기 - 상세보기 - 삭제 - 첨부파일 다운로드.
 * 	커뮤니티 게시판 CRUD. 목록·글쓰기·상세·삭제·첨부파일 다운로드 처리
 */
@Controller
@RequestMapping("/view/board")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // GET /view/board                -> 전체 목록
    // GET /view/board?industryCd=BC  -> 특정 산업만 필터
    @GetMapping
    public String list(@RequestParam(required = false) String industryCd, Model model) {
        model.addAttribute("posts", boardService.getList(industryCd));
        model.addAttribute("selectedIndustry", industryCd);
        return "board-list";
    }

    @GetMapping("/write")
    public String writeForm() {
        return "board-write";
    }

    // enctype="multipart/form-data" 로 온 폼을 처리 - file은 없어도 정상 처리됨(선택 첨부)
    @PostMapping("/write")
    public String write(@RequestParam String industryCd,
                         @RequestParam String title,
                         @RequestParam String content,
                         @RequestParam String author,
                         @RequestParam(required = false) MultipartFile file) throws IOException {
        boardService.createPost(industryCd, title, content, author, file);
        return "redirect:/view/board";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("post", boardService.getPost(id));
        return "board-detail";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) throws IOException {
        boardService.deletePost(id);
        return "redirect:/view/board";
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        var post = boardService.getPost(id);
        Path path = Paths.get(post.getAttachmentPath());
        Resource resource = new UrlResource(path.toUri());

        // 한글 파일명이 깨지지 않도록 UTF-8로 URL 인코딩 처리 (공백 처리 포함)
        String encodedFileName = URLEncoder.encode(post.getAttachmentName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                // 브라우저가 파일을 자체 해석하지 않고 다운로드
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                // 인코딩된 파일명 적용
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .body(resource);
    }
}