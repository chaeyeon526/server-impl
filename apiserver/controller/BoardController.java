package com.koreait.apiserver.controller;

import com.koreait.apiserver.dto.ApiResponse;
import com.koreait.apiserver.dto.BoardDTO;
import com.koreait.apiserver.dto.MemberDTO;
import com.koreait.apiserver.service.BoardService;

import com.koreait.apiserver.service.MemberService;
import com.koreait.apiserver.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final MemberService memberService;


     // 게시글 목록 조회 (페이징, 검색, 정렬)
     
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBoardList(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", defaultValue = "recent") String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Authentication authentication) {
        try {
            Integer currentUserId = getCurrentUserId(authentication);
            Map<String, Object> result = boardService.getBoardList(categoryId, search, sort, page, size, currentUserId);
            return ResponseEntity.ok(new ApiResponse<Map<String, Object>>(true, "게시글 목록을 조회했습니다.", result));
        } catch (Exception e) {
            log.error("게시글 목록 조회 실패", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<Map<String, Object>>(false, "게시글 목록 조회에 실패했습니다."));
        }
    }

  
   // 게시글 상세 조회
     
    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardDTO>> getBoardDetail(
            @PathVariable Integer boardId,
            Authentication authentication) {
        try {
            Integer currentUserId = getCurrentUserId(authentication);
            
            // 게시글 조회
            BoardDTO board = boardService.getBoardById(boardId, currentUserId);
            return ResponseEntity.ok(new ApiResponse<BoardDTO>(true, "게시글을 조회했습니다.", board));
        } catch (Exception e) {
            log.error("게시글 상세 조회 실패: boardId={}", boardId, e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<BoardDTO>(false, "게시글을 찾을 수 없습니다."));
        }
    }

    
    // 게시글 작성
    
    @PostMapping
    public ResponseEntity<ApiResponse<Integer>> createBoard(
            @RequestBody BoardDTO boardDTO,
            Authentication authentication,
            HttpServletRequest request) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(401)
                        .body(new ApiResponse<Integer>(false, "로그인이 필요합니다."));
            }

            Integer authorId = getCurrentUserId(authentication);
            String ipAddress = getClientIpAddress(request);
            
            Integer boardId = boardService.createBoard(boardDTO, authorId, ipAddress);
            return ResponseEntity.ok(new ApiResponse<Integer>(true, "게시글이 작성되었습니다.", boardId));
        } catch (Exception e) {
            log.error("게시글 작성 실패", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<Integer>(false, "게시글 작성에 실패했습니다."));
        }
    }

    
     // 게시글 수정
     
    @PutMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Void>> updateBoard(
            @PathVariable Integer boardId,
            @RequestBody BoardDTO boardDTO,
            Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(401)
                        .body(new ApiResponse<Void>(false, "로그인이 필요합니다."));
            }

            Integer authorId = getCurrentUserId(authentication);
            boardService.updateBoard(boardId, boardDTO, authorId);
            return ResponseEntity.ok(new ApiResponse<Void>(true, "게시글이 수정되었습니다."));
        } catch (Exception e) {
            log.error("게시글 수정 실패: boardId={}", boardId, e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<Void>(false, e.getMessage()));
        }
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(
            @PathVariable Integer boardId,
            Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(401)
                        .body(new ApiResponse<Void>(false, "로그인이 필요합니다."));
            }

            Integer authorId = getCurrentUserId(authentication);
            boardService.deleteBoard(boardId, authorId);
            return ResponseEntity.ok(new ApiResponse<Void>(true, "게시글이 삭제되었습니다."));
        } catch (Exception e) {
            log.error("게시글 삭제 실패: boardId={}", boardId, e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<Void>(false, e.getMessage()));
        }
    }
}