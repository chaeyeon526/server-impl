

-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS test1
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 데이터베이스 사용
USE test1;

-- =============================================
-- 1. 회원 테이블
-- =============================================
CREATE TABLE members (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '회원 ID (PK)',
    userid VARCHAR(100) NOT NULL UNIQUE COMMENT '아이디',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT '사용자 이메일'
    phone VARCHAR(20) COMMENT '전화번호',
    nickname VARCHAR(50) COMMENT '닉네임',
    name VARCHAR(50) NOT NULL COMMENT '이름',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호 (암호화)',
    gender ENUM('남자','여자') COMMENT '성별',
    region VARCHAR(100) COMMENT '지역',
    district VARCHAR(100) COMMENT '구/군',
    profile_image VARCHAR(500) COMMENT '프로필 이미지 경로',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '가입일',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    INDEX idx_members_description (description(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='회원 정보';


-- =============================================
-- 2. 게시글 테이블
-- =============================================
CREATE TABLE board (
    board_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '게시글 ID',
    author_id INT NOT NULL COMMENT '작성자 ID (FK)',
    title VARCHAR(255) NOT NULL COMMENT '제목',
    content TEXT NOT NULL COMMENT '내용',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '삭제 여부',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    
    FOREIGN KEY (category_id) REFERENCES board_categories(category_id) ON DELETE SET NULL,
    FOREIGN KEY (author_id) REFERENCES members(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='게시글';




