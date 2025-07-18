package com.koreait.apiserver.dao;

import com.koreait.apiserver.entity.Board;
import com.koreait.apiserver.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardDao {
    
    // 게시글 목록 조회
    List<Board> selectBoardList(@Param("categoryId") Integer categoryId,
                               @Param("search") String search,
                               @Param("sort") String sort,
                               @Param("offset") int offset,
                               @Param("size") int size,
                               @Param("currentUserId") Integer currentUserId);
    // 게시글 상세 조회
    Optional<Board> selectBoardById(@Param("boardId") Integer boardId,
                                   @Param("currentUserId") Integer currentUserId);
    
    // 게시글 작성
    int insertBoard(Board board);
    
    // 게시글 수정
    int updateBoard(Board board);
    
    // 게시글 삭제
    int deleteBoard (@Param("authorId") Integer authorId);

    // 작성자 본인 확인
    boolean isAuthor(@Param("boardId") Integer boardId, 
                    @Param("authorId") Integer authorId);
} 