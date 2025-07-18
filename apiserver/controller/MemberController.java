package com.koreait.apiserver.controller;
import com.koreait.apiserver.dto.ApiResponse;
import com.koreait.apiserver.dto.LoginResponseDTO;
import com.koreait.apiserver.dto.MemberDTO;
import com.koreait.apiserver.service.JwtService;
import com.koreait.apiserver.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtService jwtService;

    // 회원가입
    @PostMapping("/api/auth/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody MemberDTO memberDTO) {
        try {
            
            memberService.register(memberDTO);
            return ResponseEntity.ok(ApiResponse.success());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("REGISTRATION_ERROR"));
        }
    }

    // 로그인
    @PostMapping("/api/auth/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody MemberDTO memberDTO) {
        try {
            MemberDTO result = memberService.login(memberDTO.getUsername(), memberDTO.getPassword());
            if (result != null) {

                // JWT 토큰 생성
                String token = jwtService.generateToken(result.getUsername());
                
                // 로그인 응답 DTO 생성
                LoginResponseDTO loginResponse = new LoginResponseDTO();
                loginResponse.setToken(token);
                loginResponse.setUsername(result.getUsername());
                loginResponse.setEmail(result.getEmail());

                return ResponseEntity.ok(ApiResponse.success(loginResponse));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("LOGIN_FAILED"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("LOGIN_ERROR"));
        }
    }

    // 회원 정보 조회 (username 기반)
    @GetMapping("/api/members/{username}")
    public ResponseEntity<ApiResponse<MemberDTO>> getMember(@PathVariable String username) {
        try {
            MemberDTO member = memberService.getMemberByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(member));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("PROFILE_GET_ERROR"));
        }
    }

    // 회원 정보 수정
    @PutMapping("/api/members/{username}")
    public ResponseEntity<ApiResponse<MemberDTO>> updateMember(
            @PathVariable String username,
            @RequestBody MemberDTO memberDTO) {
        try {
            memberDTO.setUsername(username);
            MemberDTO updatedMember = memberService.updateMember(memberDTO);
            return ResponseEntity.ok(ApiResponse.success(updatedMember));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("PROFILE_UPDATE_ERROR"));
        }
    }

    // 현재 사용자 정보 조회 (JWT 토큰 기반)
    @GetMapping("/api/members/me")
    public ResponseEntity<ApiResponse<MemberDTO>> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("AUTHORIZATION_REQUIRED"));
            }
            
            String token = authHeader.replace("Bearer ", "");
            String username = jwtService.getUsernameFromToken(token);
            
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("INVALID_TOKEN"));
            }
            
            MemberDTO member = memberService.getMemberByUsername(username);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("USER_NOT_FOUND"));
            }
            
            return ResponseEntity.ok(ApiResponse.success(member));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("TOKEN_VALIDATION_ERROR"));
        }
    }

    // 현재 사용자 정보 수정 (JWT 토큰 기반)
    @PutMapping("/api/members/me")
    public ResponseEntity<ApiResponse<MemberDTO>> updateCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody MemberDTO memberDTO) {
        try {
            log.info("사용자 정보 수정 요청 시작");
            log.info("요청 데이터: {}", memberDTO);
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Authorization 헤더가 없거나 잘못됨");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("AUTHORIZATION_REQUIRED"));
            }
            
            String token = authHeader.replace("Bearer ", "");
            String username = jwtService.getUsernameFromToken(token);
            log.info("토큰에서 추출한 사용자명: {}", username);
            
            if (username == null) {
                log.warn("토큰에서 사용자명을 추출할 수 없음");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("INVALID_TOKEN"));
            }
            
            // 현재 사용자의 username으로 설정
            memberDTO.setUsername(username);
            log.info("업데이트할 사용자: {}", username);
            
            MemberDTO updatedMember = memberService.updateMember(memberDTO);
            log.info("사용자 정보 수정 완료");
            return ResponseEntity.ok(ApiResponse.success(updatedMember));
        } catch (Exception e) {
            log.error("사용자 정보 수정 실패", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("PROFILE_UPDATE_ERROR"));
        }
    }