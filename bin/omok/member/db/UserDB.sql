CREATE TABLE user_info (
    id VARCHAR2(50) PRIMARY KEY,        -- 아이디
    password VARCHAR2(100) NOT NULL,    -- 비밀번호
    name VARCHAR2(100) NOT NULL,        -- 이름
    nickname VARCHAR2(50) NOT NULL,     -- 닉네임
    email VARCHAR2(100) NOT NULL,       -- 이메일
    birth_year INT NOT NULL,            -- 생년
    birth_month INT NOT NULL,           -- 생월
    birth_day INT NOT NULL,             -- 생일
    gender CHAR(1) CHECK (gender IN ('M', 'F')), -- 성별 ('M' or 'F')
    phone_number VARCHAR2(20) NOT NULL, -- 전화번호
    postal_code VARCHAR2(10),           -- 우편번호
    address VARCHAR2(255),              -- 주소
    detailed_address VARCHAR2(255),     -- 상세 주소
    profile_image BLOB,                 -- 프로필 이미지 (BLOB 형태)
    CONSTRAINT chk_gender CHECK (gender IN ('M', 'F'))  -- 성별 유효성 검사
);

SELECT * FROM user_info;
SELECT id, password FROM user_info;
SELECT id, phone_number, profile_image FROM user_info;
SELECT id, nickname FROM user_info;
SELECT id, address FROM user_info;
SELECT score FROM user_info;

-- 이미 추가 완 --
ALTER TABLE user_info ADD intro VARCHAR2(500);

ALTER TABLE user_info ADD (isBlocked NUMBER(1));

ALTER TABLE user_info ADD status VARCHAR2(10) DEFAULT 'ACTIVE';
ALTER TABLE user_info ADD blocked_date TIMESTAMP;
ALTER TABLE user_info ADD deleted_date TIMESTAMP;

ALTER TABLE user_info ADD reason VARCHAR2(255);
-- 이미 추가 완 --

-- 추가 완료
ALTER TABLE user_info ADD score INT DEFAULT 0;
ALTER TABLE user_info ADD rank INT;
-- 추가 완료
ALTER TABLE user_info ADD role VARCHAR(20) DEFAULT 'USER';

UPDATE user_info
SET role = 'ADMIN'
WHERE id = 'aaaa';

ALTER TABLE user_info ADD saved_character VARCHAR(50);


DROP TABLE user_info;