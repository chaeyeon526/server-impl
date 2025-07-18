package com.koreait.apiserver.dao;

import com.koreait.apiserver.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Mapper
public interface MemberDao {

    // 회원 등록
    int insertMember(Member member);

    // 회원 조회 (ID로)
    Optional<Member> findById(Integer id);

    // 회원 조회 (username으로)
    Optional<Member> findByUsername(String username);

    // 모든 회원 조회
    List<Member> findAll();

    // 회원 정보 수정
    int updateMember(Member member);

    // 회원 삭제
    int deleteMember(Integer id);

    // username 중복 확인
    boolean existsByUsername(String username);

    // email 중복 확인
    boolean existsByEmail(String email);
}