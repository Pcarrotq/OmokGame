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


DROP TABLE user_info;