-- YouLai_Boot Database (Oracle 11g and below)
-- Copyright (c) 2021-present, youlai.tech
-- Oracle 11g 이하 버전용 DDL (시퀀스 + 트리거 방식)

-- ============================
-- 1. 테이블스페이스 및 사용자 생성 (DBA 권한 필요)
-- ============================
-- CREATE TABLESPACE youlai_ts DATAFILE 'youlai_ts.dbf' SIZE 100M AUTOEXTEND ON;
-- CREATE USER youlai IDENTIFIED BY "123456" DEFAULT TABLESPACE youlai_ts;
-- GRANT CONNECT, RESOURCE, CREATE VIEW, CREATE SEQUENCE, CREATE TRIGGER TO youlai;
-- ALTER USER youlai QUOTA UNLIMITED ON youlai_ts;

-- ============================
-- 2. 기존 객체 삭제 (순서 중요: 트리거 -> 테이블 -> 시퀀스)
-- ============================

-- 트리거 삭제
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER trg_sys_dept_id'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER trg_sys_dict_id'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER trg_sys_dict_item_id'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER trg_sys_menu_id'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER trg_sys_role_id'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER trg_sys_user_id'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER trg_sys_log_id'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER trg_gen_config_id'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER trg_gen_field_config_id'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER trg_sys_config_id'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER trg_sys_notice_id'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER trg_sys_user_notice_id'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TRIGGER trg_ai_command_record_id'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

-- 테이블 삭제
BEGIN EXECUTE IMMEDIATE 'DROP TABLE ai_command_record CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE sys_user_notice CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE sys_notice CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE sys_config CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE gen_field_config CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE gen_config CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE sys_log CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE sys_user_role CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE sys_user CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE sys_role_menu CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE sys_role CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE sys_menu CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE sys_dict_item CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE sys_dict CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE sys_dept CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

-- 시퀀스 삭제
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_sys_dept'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_sys_dict'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_sys_dict_item'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_sys_menu'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_sys_role'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_sys_user'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_sys_log'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_gen_config'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_gen_field_config'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_sys_config'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_sys_notice'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_sys_user_notice'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_ai_command_record'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

-- ============================
-- 3. 시퀀스 생성
-- ============================
CREATE SEQUENCE seq_sys_dept START WITH 100 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_sys_dict START WITH 100 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_sys_dict_item START WITH 100 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_sys_menu START WITH 200 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_sys_role START WITH 100 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_sys_user START WITH 100 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_sys_log START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_gen_config START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_gen_field_config START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_sys_config START WITH 100 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_sys_notice START WITH 100 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_sys_user_notice START WITH 100 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE seq_ai_command_record START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- ============================
-- 4. 테이블 생성
-- ============================

-- ----------------------------
-- Table: sys_dept (부서 테이블)
-- ----------------------------
CREATE TABLE sys_dept (
    id              NUMBER(19) NOT NULL,
    name            VARCHAR2(100) NOT NULL,
    code            VARCHAR2(100) NOT NULL,
    parent_id       NUMBER(19) DEFAULT 0,
    tree_path       VARCHAR2(255) NOT NULL,
    sort            NUMBER(5) DEFAULT 0,
    status          NUMBER(3) DEFAULT 1,
    create_by       NUMBER(19),
    create_time     DATE,
    update_by       NUMBER(19),
    update_time     DATE,
    is_deleted      NUMBER(3) DEFAULT 0,
    CONSTRAINT pk_sys_dept PRIMARY KEY (id),
    CONSTRAINT uk_sys_dept_code UNIQUE (code)
);

COMMENT ON TABLE sys_dept IS '부서 테이블';
COMMENT ON COLUMN sys_dept.id IS '기본키';
COMMENT ON COLUMN sys_dept.name IS '부서 이름';
COMMENT ON COLUMN sys_dept.code IS '부서 코드';
COMMENT ON COLUMN sys_dept.parent_id IS '부모 노드 ID';
COMMENT ON COLUMN sys_dept.tree_path IS '부모 노드 ID 경로';
COMMENT ON COLUMN sys_dept.sort IS '표시 순서';
COMMENT ON COLUMN sys_dept.status IS '상태(1-정상 0-비활성)';
COMMENT ON COLUMN sys_dept.create_by IS '생성자 ID';
COMMENT ON COLUMN sys_dept.create_time IS '생성 시간';
COMMENT ON COLUMN sys_dept.update_by IS '수정자 ID';
COMMENT ON COLUMN sys_dept.update_time IS '수정 시간';
COMMENT ON COLUMN sys_dept.is_deleted IS '논리 삭제 플래그(1-삭제됨 0-삭제안됨)';

CREATE OR REPLACE TRIGGER trg_sys_dept_id
BEFORE INSERT ON sys_dept
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
    SELECT seq_sys_dept.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- ----------------------------
-- Table: sys_dict (사전 테이블)
-- ----------------------------
CREATE TABLE sys_dict (
    id              NUMBER(19) NOT NULL,
    dict_code       VARCHAR2(50),
    name            VARCHAR2(50),
    status          NUMBER(3) DEFAULT 0,
    remark          VARCHAR2(255),
    create_time     DATE,
    create_by       NUMBER(19),
    update_time     DATE,
    update_by       NUMBER(19),
    is_deleted      NUMBER(3) DEFAULT 0,
    CONSTRAINT pk_sys_dict PRIMARY KEY (id)
);

CREATE INDEX idx_sys_dict_code ON sys_dict(dict_code);

COMMENT ON TABLE sys_dict IS '사전 테이블';
COMMENT ON COLUMN sys_dict.id IS '기본키';
COMMENT ON COLUMN sys_dict.dict_code IS '유형 코드';
COMMENT ON COLUMN sys_dict.name IS '유형 이름';
COMMENT ON COLUMN sys_dict.status IS '상태(0:정상;1:비활성)';
COMMENT ON COLUMN sys_dict.remark IS '비고';
COMMENT ON COLUMN sys_dict.create_time IS '생성 시간';
COMMENT ON COLUMN sys_dict.create_by IS '생성자 ID';
COMMENT ON COLUMN sys_dict.update_time IS '수정 시간';
COMMENT ON COLUMN sys_dict.update_by IS '수정자 ID';
COMMENT ON COLUMN sys_dict.is_deleted IS '삭제 여부(1-삭제됨, 0-삭제안됨)';

CREATE OR REPLACE TRIGGER trg_sys_dict_id
BEFORE INSERT ON sys_dict
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
    SELECT seq_sys_dict.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- ----------------------------
-- Table: sys_dict_item (사전 항목 테이블)
-- ----------------------------
CREATE TABLE sys_dict_item (
    id              NUMBER(19) NOT NULL,
    dict_code       VARCHAR2(50),
    value           VARCHAR2(50),
    label           VARCHAR2(100),
    tag_type        VARCHAR2(50),
    status          NUMBER(3) DEFAULT 0,
    sort            NUMBER(10) DEFAULT 0,
    remark          VARCHAR2(255),
    create_time     DATE,
    create_by       NUMBER(19),
    update_time     DATE,
    update_by       NUMBER(19),
    CONSTRAINT pk_sys_dict_item PRIMARY KEY (id)
);

COMMENT ON TABLE sys_dict_item IS '사전 항목 테이블';
COMMENT ON COLUMN sys_dict_item.id IS '기본키';
COMMENT ON COLUMN sys_dict_item.dict_code IS '연관 사전 코드';
COMMENT ON COLUMN sys_dict_item.value IS '사전 항목 값';
COMMENT ON COLUMN sys_dict_item.label IS '사전 항목 레이블';
COMMENT ON COLUMN sys_dict_item.tag_type IS '태그 유형';
COMMENT ON COLUMN sys_dict_item.status IS '상태(1-정상, 0-비활성)';
COMMENT ON COLUMN sys_dict_item.sort IS '정렬';
COMMENT ON COLUMN sys_dict_item.remark IS '비고';
COMMENT ON COLUMN sys_dict_item.create_time IS '생성 시간';
COMMENT ON COLUMN sys_dict_item.create_by IS '생성자 ID';
COMMENT ON COLUMN sys_dict_item.update_time IS '수정 시간';
COMMENT ON COLUMN sys_dict_item.update_by IS '수정자 ID';

CREATE OR REPLACE TRIGGER trg_sys_dict_item_id
BEFORE INSERT ON sys_dict_item
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
    SELECT seq_sys_dict_item.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- ----------------------------
-- Table: sys_menu (메뉴 관리)
-- ----------------------------
CREATE TABLE sys_menu (
    id              NUMBER(19) NOT NULL,
    parent_id       NUMBER(19) NOT NULL,
    tree_path       VARCHAR2(255),
    name            VARCHAR2(64) NOT NULL,
    type            NUMBER(3) NOT NULL,
    route_name      VARCHAR2(255),
    route_path      VARCHAR2(128),
    component       VARCHAR2(128),
    perm            VARCHAR2(128),
    always_show     NUMBER(3) DEFAULT 0,
    keep_alive      NUMBER(3) DEFAULT 0,
    visible         NUMBER(3) DEFAULT 1,
    sort            NUMBER(10) DEFAULT 0,
    icon            VARCHAR2(64),
    redirect        VARCHAR2(128),
    create_time     DATE,
    update_time     DATE,
    params          VARCHAR2(255),
    CONSTRAINT pk_sys_menu PRIMARY KEY (id)
);

COMMENT ON TABLE sys_menu IS '메뉴 관리';
COMMENT ON COLUMN sys_menu.id IS 'ID';
COMMENT ON COLUMN sys_menu.parent_id IS '부모 메뉴 ID';
COMMENT ON COLUMN sys_menu.tree_path IS '부모 노드 ID 경로';
COMMENT ON COLUMN sys_menu.name IS '메뉴 이름';
COMMENT ON COLUMN sys_menu.type IS '메뉴 유형(1-메뉴 2-디렉토리 3-외부링크 4-버튼)';
COMMENT ON COLUMN sys_menu.route_name IS '라우트 이름';
COMMENT ON COLUMN sys_menu.route_path IS '라우트 경로';
COMMENT ON COLUMN sys_menu.component IS '컴포넌트 경로';
COMMENT ON COLUMN sys_menu.perm IS '버튼 권한 식별자';
COMMENT ON COLUMN sys_menu.always_show IS '자식 라우트가 하나일 때도 항상 표시(1-예 0-아니오)';
COMMENT ON COLUMN sys_menu.keep_alive IS '페이지 캐시 활성화(1-예 0-아니오)';
COMMENT ON COLUMN sys_menu.visible IS '표시 상태(1-표시 0-숨김)';
COMMENT ON COLUMN sys_menu.sort IS '정렬';
COMMENT ON COLUMN sys_menu.icon IS '메뉴 아이콘';
COMMENT ON COLUMN sys_menu.redirect IS '리다이렉트 경로';
COMMENT ON COLUMN sys_menu.create_time IS '생성 시간';
COMMENT ON COLUMN sys_menu.update_time IS '수정 시간';
COMMENT ON COLUMN sys_menu.params IS '라우트 파라미터';

CREATE OR REPLACE TRIGGER trg_sys_menu_id
BEFORE INSERT ON sys_menu
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
    SELECT seq_sys_menu.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- ----------------------------
-- Table: sys_role (역할 테이블)
-- ----------------------------
CREATE TABLE sys_role (
    id              NUMBER(19) NOT NULL,
    name            VARCHAR2(64) NOT NULL,
    code            VARCHAR2(32) NOT NULL,
    sort            NUMBER(10),
    status          NUMBER(3) DEFAULT 1,
    data_scope      NUMBER(3),
    create_by       NUMBER(19),
    create_time     DATE,
    update_by       NUMBER(19),
    update_time     DATE,
    is_deleted      NUMBER(3) DEFAULT 0,
    CONSTRAINT pk_sys_role PRIMARY KEY (id),
    CONSTRAINT uk_sys_role_name UNIQUE (name),
    CONSTRAINT uk_sys_role_code UNIQUE (code)
);

COMMENT ON TABLE sys_role IS '역할 테이블';
COMMENT ON COLUMN sys_role.id IS 'ID';
COMMENT ON COLUMN sys_role.name IS '역할 이름';
COMMENT ON COLUMN sys_role.code IS '역할 코드';
COMMENT ON COLUMN sys_role.sort IS '표시 순서';
COMMENT ON COLUMN sys_role.status IS '역할 상태(1-정상 0-중지)';
COMMENT ON COLUMN sys_role.data_scope IS '데이터 권한(1-모든데이터 2-부서및하위 3-본부서 4-본인)';
COMMENT ON COLUMN sys_role.create_by IS '생성자 ID';
COMMENT ON COLUMN sys_role.create_time IS '생성 시간';
COMMENT ON COLUMN sys_role.update_by IS '수정자 ID';
COMMENT ON COLUMN sys_role.update_time IS '수정 시간';
COMMENT ON COLUMN sys_role.is_deleted IS '논리 삭제 플래그(0-삭제안됨 1-삭제됨)';

CREATE OR REPLACE TRIGGER trg_sys_role_id
BEFORE INSERT ON sys_role
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
    SELECT seq_sys_role.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- ----------------------------
-- Table: sys_role_menu (역할 메뉴 연관 테이블)
-- ----------------------------
CREATE TABLE sys_role_menu (
    role_id         NUMBER(19) NOT NULL,
    menu_id         NUMBER(19) NOT NULL,
    CONSTRAINT uk_sys_role_menu UNIQUE (role_id, menu_id)
);

COMMENT ON TABLE sys_role_menu IS '역할 메뉴 연관 테이블';
COMMENT ON COLUMN sys_role_menu.role_id IS '역할 ID';
COMMENT ON COLUMN sys_role_menu.menu_id IS '메뉴 ID';

-- ----------------------------
-- Table: sys_user (사용자 정보 테이블)
-- ----------------------------
CREATE TABLE sys_user (
    id              NUMBER(19) NOT NULL,
    username        VARCHAR2(64),
    nickname        VARCHAR2(64),
    gender          NUMBER(3) DEFAULT 1,
    password        VARCHAR2(100),
    dept_id         NUMBER(10),
    avatar          VARCHAR2(255),
    mobile          VARCHAR2(20),
    status          NUMBER(3) DEFAULT 1,
    email           VARCHAR2(128),
    create_time     DATE,
    create_by       NUMBER(19),
    update_time     DATE,
    update_by       NUMBER(19),
    is_deleted      NUMBER(3) DEFAULT 0,
    openid          CHAR(28),
    CONSTRAINT pk_sys_user PRIMARY KEY (id)
);

CREATE INDEX idx_sys_user_username ON sys_user(username);

COMMENT ON TABLE sys_user IS '사용자 정보 테이블';
COMMENT ON COLUMN sys_user.id IS 'ID';
COMMENT ON COLUMN sys_user.username IS '사용자명';
COMMENT ON COLUMN sys_user.nickname IS '닉네임';
COMMENT ON COLUMN sys_user.gender IS '성별(1-남성 2-여성 0-비공개)';
COMMENT ON COLUMN sys_user.password IS '비밀번호';
COMMENT ON COLUMN sys_user.dept_id IS '부서 ID';
COMMENT ON COLUMN sys_user.avatar IS '사용자 아바타';
COMMENT ON COLUMN sys_user.mobile IS '연락처';
COMMENT ON COLUMN sys_user.status IS '상태(1-정상 0-비활성)';
COMMENT ON COLUMN sys_user.email IS '사용자 이메일';
COMMENT ON COLUMN sys_user.create_time IS '생성 시간';
COMMENT ON COLUMN sys_user.create_by IS '생성자 ID';
COMMENT ON COLUMN sys_user.update_time IS '수정 시간';
COMMENT ON COLUMN sys_user.update_by IS '수정자 ID';
COMMENT ON COLUMN sys_user.is_deleted IS '논리 삭제 플래그(0-삭제안됨 1-삭제됨)';
COMMENT ON COLUMN sys_user.openid IS '위챗 openid';

CREATE OR REPLACE TRIGGER trg_sys_user_id
BEFORE INSERT ON sys_user
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
    SELECT seq_sys_user.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- ----------------------------
-- Table: sys_user_role (사용자 역할 연관 테이블)
-- ----------------------------
CREATE TABLE sys_user_role (
    user_id         NUMBER(19) NOT NULL,
    role_id         NUMBER(19) NOT NULL,
    CONSTRAINT pk_sys_user_role PRIMARY KEY (user_id, role_id)
);

COMMENT ON TABLE sys_user_role IS '사용자 역할 연관 테이블';
COMMENT ON COLUMN sys_user_role.user_id IS '사용자 ID';
COMMENT ON COLUMN sys_user_role.role_id IS '역할 ID';

-- ----------------------------
-- Table: sys_log (시스템 로그 테이블)
-- ----------------------------
CREATE TABLE sys_log (
    id              NUMBER(19) NOT NULL,
    module          VARCHAR2(50) NOT NULL,
    request_method  VARCHAR2(64) NOT NULL,
    request_params  CLOB,
    response_content CLOB,
    content         VARCHAR2(255) NOT NULL,
    request_uri     VARCHAR2(255),
    method          VARCHAR2(255),
    ip              VARCHAR2(45),
    province        VARCHAR2(100),
    city            VARCHAR2(100),
    execution_time  NUMBER(19),
    browser         VARCHAR2(100),
    browser_version VARCHAR2(100),
    os              VARCHAR2(100),
    create_by       NUMBER(19),
    create_time     DATE,
    is_deleted      NUMBER(3) DEFAULT 0,
    CONSTRAINT pk_sys_log PRIMARY KEY (id)
);

CREATE INDEX idx_sys_log_create_time ON sys_log(create_time);

COMMENT ON TABLE sys_log IS '시스템 로그 테이블';
COMMENT ON COLUMN sys_log.id IS '기본키';
COMMENT ON COLUMN sys_log.module IS '로그 모듈';
COMMENT ON COLUMN sys_log.request_method IS '요청 방식';
COMMENT ON COLUMN sys_log.request_params IS '요청 파라미터';
COMMENT ON COLUMN sys_log.response_content IS '반환 파라미터';
COMMENT ON COLUMN sys_log.content IS '로그 내용';
COMMENT ON COLUMN sys_log.request_uri IS '요청 경로';
COMMENT ON COLUMN sys_log.method IS '메서드명';
COMMENT ON COLUMN sys_log.ip IS 'IP 주소';
COMMENT ON COLUMN sys_log.province IS '도';
COMMENT ON COLUMN sys_log.city IS '시';
COMMENT ON COLUMN sys_log.execution_time IS '실행 시간(ms)';
COMMENT ON COLUMN sys_log.browser IS '브라우저';
COMMENT ON COLUMN sys_log.browser_version IS '브라우저 버전';
COMMENT ON COLUMN sys_log.os IS '운영체제';
COMMENT ON COLUMN sys_log.create_by IS '생성자 ID';
COMMENT ON COLUMN sys_log.create_time IS '생성 시간';
COMMENT ON COLUMN sys_log.is_deleted IS '논리 삭제 플래그(1-삭제됨 0-삭제안됨)';

CREATE OR REPLACE TRIGGER trg_sys_log_id
BEFORE INSERT ON sys_log
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
    SELECT seq_sys_log.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- ----------------------------
-- Table: gen_config (코드 생성 기본 설정 테이블)
-- ----------------------------
CREATE TABLE gen_config (
    id                  NUMBER(19) NOT NULL,
    table_name          VARCHAR2(100) NOT NULL,
    module_name         VARCHAR2(100),
    package_name        VARCHAR2(255) NOT NULL,
    business_name       VARCHAR2(100) NOT NULL,
    entity_name         VARCHAR2(100) NOT NULL,
    author              VARCHAR2(50) NOT NULL,
    parent_menu_id      NUMBER(19),
    remove_table_prefix VARCHAR2(20),
    page_type           VARCHAR2(20),
    create_time         DATE,
    update_time         DATE,
    is_deleted          NUMBER(3) DEFAULT 0,
    CONSTRAINT pk_gen_config PRIMARY KEY (id),
    CONSTRAINT uk_gen_config_table UNIQUE (table_name)
);

COMMENT ON TABLE gen_config IS '코드 생성 기본 설정 테이블';
COMMENT ON COLUMN gen_config.id IS 'ID';
COMMENT ON COLUMN gen_config.table_name IS '테이블명';
COMMENT ON COLUMN gen_config.module_name IS '모듈명';
COMMENT ON COLUMN gen_config.package_name IS '패키지명';
COMMENT ON COLUMN gen_config.business_name IS '비즈니스명';
COMMENT ON COLUMN gen_config.entity_name IS '엔티티 클래스명';
COMMENT ON COLUMN gen_config.author IS '작성자';
COMMENT ON COLUMN gen_config.parent_menu_id IS '상위 메뉴 ID';
COMMENT ON COLUMN gen_config.remove_table_prefix IS '제거할 테이블 접두사';
COMMENT ON COLUMN gen_config.page_type IS '페이지 유형(classic|curd)';
COMMENT ON COLUMN gen_config.create_time IS '생성 시간';
COMMENT ON COLUMN gen_config.update_time IS '수정 시간';
COMMENT ON COLUMN gen_config.is_deleted IS '삭제 여부';

CREATE OR REPLACE TRIGGER trg_gen_config_id
BEFORE INSERT ON gen_config
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
    SELECT seq_gen_config.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- ----------------------------
-- Table: gen_field_config (코드 생성 필드 설정 테이블)
-- ----------------------------
CREATE TABLE gen_field_config (
    id              NUMBER(19) NOT NULL,
    config_id       NUMBER(19) NOT NULL,
    column_name     VARCHAR2(100),
    column_type     VARCHAR2(50),
    column_length   NUMBER(10),
    field_name      VARCHAR2(100) NOT NULL,
    field_type      VARCHAR2(100),
    field_sort      NUMBER(10),
    field_comment   VARCHAR2(255),
    max_length      NUMBER(10),
    is_required     NUMBER(3),
    is_show_in_list NUMBER(3) DEFAULT 0,
    is_show_in_form NUMBER(3) DEFAULT 0,
    is_show_in_query NUMBER(3) DEFAULT 0,
    query_type      NUMBER(3),
    form_type       NUMBER(3),
    dict_type       VARCHAR2(50),
    create_time     DATE,
    update_time     DATE,
    CONSTRAINT pk_gen_field_config PRIMARY KEY (id)
);

CREATE INDEX idx_gen_field_config_id ON gen_field_config(config_id);

COMMENT ON TABLE gen_field_config IS '코드 생성 필드 설정 테이블';
COMMENT ON COLUMN gen_field_config.id IS 'ID';
COMMENT ON COLUMN gen_field_config.config_id IS '연관 설정 ID';
COMMENT ON COLUMN gen_field_config.column_name IS '컬럼명';
COMMENT ON COLUMN gen_field_config.column_type IS '컬럼 유형';
COMMENT ON COLUMN gen_field_config.column_length IS '컬럼 길이';
COMMENT ON COLUMN gen_field_config.field_name IS '필드명';
COMMENT ON COLUMN gen_field_config.field_type IS '필드 유형';
COMMENT ON COLUMN gen_field_config.field_sort IS '필드 정렬';
COMMENT ON COLUMN gen_field_config.field_comment IS '필드 설명';
COMMENT ON COLUMN gen_field_config.max_length IS '최대 길이';
COMMENT ON COLUMN gen_field_config.is_required IS '필수 여부';
COMMENT ON COLUMN gen_field_config.is_show_in_list IS '목록에 표시 여부';
COMMENT ON COLUMN gen_field_config.is_show_in_form IS '폼에 표시 여부';
COMMENT ON COLUMN gen_field_config.is_show_in_query IS '조회 조건에 표시 여부';
COMMENT ON COLUMN gen_field_config.query_type IS '조회 방식';
COMMENT ON COLUMN gen_field_config.form_type IS '폼 유형';
COMMENT ON COLUMN gen_field_config.dict_type IS '사전 유형';
COMMENT ON COLUMN gen_field_config.create_time IS '생성 시간';
COMMENT ON COLUMN gen_field_config.update_time IS '수정 시간';

CREATE OR REPLACE TRIGGER trg_gen_field_config_id
BEFORE INSERT ON gen_field_config
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
    SELECT seq_gen_field_config.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- ----------------------------
-- Table: sys_config (시스템 설정 테이블)
-- ----------------------------
CREATE TABLE sys_config (
    id              NUMBER(19) NOT NULL,
    config_name     VARCHAR2(50) NOT NULL,
    config_key      VARCHAR2(50) NOT NULL,
    config_value    VARCHAR2(100) NOT NULL,
    remark          VARCHAR2(255),
    create_time     DATE,
    create_by       NUMBER(19),
    update_time     DATE,
    update_by       NUMBER(19),
    is_deleted      NUMBER(3) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_sys_config PRIMARY KEY (id)
);

COMMENT ON TABLE sys_config IS '시스템 설정 테이블';
COMMENT ON COLUMN sys_config.id IS 'ID';
COMMENT ON COLUMN sys_config.config_name IS '설정 이름';
COMMENT ON COLUMN sys_config.config_key IS '설정 key';
COMMENT ON COLUMN sys_config.config_value IS '설정 값';
COMMENT ON COLUMN sys_config.remark IS '비고';
COMMENT ON COLUMN sys_config.create_time IS '생성 시간';
COMMENT ON COLUMN sys_config.create_by IS '생성자 ID';
COMMENT ON COLUMN sys_config.update_time IS '수정 시간';
COMMENT ON COLUMN sys_config.update_by IS '수정자 ID';
COMMENT ON COLUMN sys_config.is_deleted IS '논리 삭제 플래그(0-삭제안됨 1-삭제됨)';

CREATE OR REPLACE TRIGGER trg_sys_config_id
BEFORE INSERT ON sys_config
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
    SELECT seq_sys_config.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- ----------------------------
-- Table: sys_notice (공지사항 테이블)
-- ----------------------------
CREATE TABLE sys_notice (
    id              NUMBER(19) NOT NULL,
    title           VARCHAR2(500),
    content         CLOB,
    notice_type            NUMBER(3) NOT NULL,
    notice_level           VARCHAR2(5) NOT NULL,
    target_type     NUMBER(3) NOT NULL,
    target_user_ids VARCHAR2(255),
    publisher_id    NUMBER(19),
    publish_status  NUMBER(3) DEFAULT 0,
    publish_time    DATE,
    revoke_time     DATE,
    create_by       NUMBER(19) NOT NULL,
    create_time     DATE NOT NULL,
    update_by       NUMBER(19),
    update_time     DATE,
    is_deleted      NUMBER(3) DEFAULT 0,
    CONSTRAINT pk_sys_notice PRIMARY KEY (id)
);

COMMENT ON TABLE sys_notice IS '공지사항 테이블';
COMMENT ON COLUMN sys_notice.id IS 'ID';
COMMENT ON COLUMN sys_notice.title IS '공지 제목';
COMMENT ON COLUMN sys_notice.content IS '공지 내용';
COMMENT ON COLUMN sys_notice.notice_type IS '공지 유형';
COMMENT ON COLUMN sys_notice.notice_level IS '공지 수준';
COMMENT ON COLUMN sys_notice.target_type IS '대상 유형(1: 전체, 2: 지정)';
COMMENT ON COLUMN sys_notice.target_user_ids IS '대상자 ID 집합';
COMMENT ON COLUMN sys_notice.publisher_id IS '발행자 ID';
COMMENT ON COLUMN sys_notice.publish_status IS '발행 상태(0: 미발행, 1: 발행됨, -1: 철회됨)';
COMMENT ON COLUMN sys_notice.publish_time IS '발행 시간';
COMMENT ON COLUMN sys_notice.revoke_time IS '철회 시간';
COMMENT ON COLUMN sys_notice.create_by IS '생성자 ID';
COMMENT ON COLUMN sys_notice.create_time IS '생성 시간';
COMMENT ON COLUMN sys_notice.update_by IS '수정자 ID';
COMMENT ON COLUMN sys_notice.update_time IS '수정 시간';
COMMENT ON COLUMN sys_notice.is_deleted IS '삭제 여부(0: 삭제안됨, 1: 삭제됨)';

CREATE OR REPLACE TRIGGER trg_sys_notice_id
BEFORE INSERT ON sys_notice
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
    SELECT seq_sys_notice.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- ----------------------------
-- Table: sys_user_notice (사용자 공지사항 테이블)
-- ----------------------------
CREATE TABLE sys_user_notice (
    id              NUMBER(19) NOT NULL,
    notice_id       NUMBER(19) NOT NULL,
    user_id         NUMBER(19) NOT NULL,
    is_read         NUMBER(19) DEFAULT 0,
    read_time       DATE,
    create_time     DATE NOT NULL,
    update_time     DATE,
    is_deleted      NUMBER(3) DEFAULT 0,
    CONSTRAINT pk_sys_user_notice PRIMARY KEY (id)
);

COMMENT ON TABLE sys_user_notice IS '사용자 공지사항 테이블';
COMMENT ON COLUMN sys_user_notice.id IS 'id';
COMMENT ON COLUMN sys_user_notice.notice_id IS '공지 id';
COMMENT ON COLUMN sys_user_notice.user_id IS '사용자 id';
COMMENT ON COLUMN sys_user_notice.is_read IS '읽음 상태(0: 읽지않음, 1: 읽음)';
COMMENT ON COLUMN sys_user_notice.read_time IS '읽은 시간';
COMMENT ON COLUMN sys_user_notice.create_time IS '생성 시간';
COMMENT ON COLUMN sys_user_notice.update_time IS '수정 시간';
COMMENT ON COLUMN sys_user_notice.is_deleted IS '논리 삭제(0: 삭제안됨, 1: 삭제됨)';

CREATE OR REPLACE TRIGGER trg_sys_user_notice_id
BEFORE INSERT ON sys_user_notice
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
    SELECT seq_sys_user_notice.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- ----------------------------
-- Table: ai_command_record (AI 명령 기록 테이블)
-- ----------------------------
CREATE TABLE ai_command_record (
    id                      NUMBER(19) NOT NULL,
    user_id                 NUMBER(19),
    username                VARCHAR2(64),
    original_command        CLOB,
    provider                VARCHAR2(32),
    model                   VARCHAR2(64),
    parse_success           NUMBER(3),
    function_calls          CLOB,
    explanation             VARCHAR2(500),
    confidence              NUMBER(3,2),
    parse_error_message     CLOB,
    input_tokens            NUMBER(10),
    output_tokens           NUMBER(10),
    total_tokens            NUMBER(10),
    parse_time              NUMBER(19),
    function_name           VARCHAR2(255),
    function_arguments      CLOB,
    execute_status          VARCHAR2(20),
    execute_result          CLOB,
    execute_error_message   CLOB,
    affected_rows           NUMBER(10),
    is_dangerous            NUMBER(3) DEFAULT 0,
    requires_confirmation   NUMBER(3) DEFAULT 0,
    user_confirmed          NUMBER(3),
    idempotency_key         VARCHAR2(128),
    execution_time          NUMBER(19),
    ip_address              VARCHAR2(128),
    user_agent              VARCHAR2(512),
    current_route           VARCHAR2(255),
    create_time             DATE DEFAULT SYSDATE,
    update_time             DATE,
    remark                  VARCHAR2(500),
    CONSTRAINT pk_ai_command_record PRIMARY KEY (id),
    CONSTRAINT uk_ai_cmd_idempotency UNIQUE (idempotency_key)
);

CREATE INDEX idx_ai_cmd_user_id ON ai_command_record(user_id);
CREATE INDEX idx_ai_cmd_create_time ON ai_command_record(create_time);
CREATE INDEX idx_ai_cmd_provider ON ai_command_record(provider);
CREATE INDEX idx_ai_cmd_model ON ai_command_record(model);
CREATE INDEX idx_ai_cmd_parse_success ON ai_command_record(parse_success);
CREATE INDEX idx_ai_cmd_execute_status ON ai_command_record(execute_status);
CREATE INDEX idx_ai_cmd_is_dangerous ON ai_command_record(is_dangerous);

COMMENT ON TABLE ai_command_record IS 'AI 명령 기록 테이블';
COMMENT ON COLUMN ai_command_record.id IS '기본키 ID';
COMMENT ON COLUMN ai_command_record.user_id IS '사용자 ID';
COMMENT ON COLUMN ai_command_record.username IS '사용자명';
COMMENT ON COLUMN ai_command_record.original_command IS '원본 명령';
COMMENT ON COLUMN ai_command_record.provider IS 'AI 공급자';
COMMENT ON COLUMN ai_command_record.model IS 'AI 모델';
COMMENT ON COLUMN ai_command_record.parse_success IS '파싱 성공 여부(0-실패, 1-성공)';
COMMENT ON COLUMN ai_command_record.function_calls IS '파싱된 함수 호출 목록(JSON)';
COMMENT ON COLUMN ai_command_record.explanation IS 'AI의 이해 설명';
COMMENT ON COLUMN ai_command_record.confidence IS '신뢰도(0.00-1.00)';
COMMENT ON COLUMN ai_command_record.parse_error_message IS '파싱 오류 메시지';
COMMENT ON COLUMN ai_command_record.input_tokens IS '입력 토큰 수';
COMMENT ON COLUMN ai_command_record.output_tokens IS '출력 토큰 수';
COMMENT ON COLUMN ai_command_record.total_tokens IS '총 토큰 수';
COMMENT ON COLUMN ai_command_record.parse_time IS '파싱 소요 시간(밀리초)';
COMMENT ON COLUMN ai_command_record.function_name IS '실행된 함수명';
COMMENT ON COLUMN ai_command_record.function_arguments IS '함수 인자(JSON)';
COMMENT ON COLUMN ai_command_record.execute_status IS '실행 상태';
COMMENT ON COLUMN ai_command_record.execute_result IS '실행 결과(JSON)';
COMMENT ON COLUMN ai_command_record.execute_error_message IS '실행 오류 메시지';
COMMENT ON COLUMN ai_command_record.affected_rows IS '영향받은 레코드 수';
COMMENT ON COLUMN ai_command_record.is_dangerous IS '위험 작업 여부(0-아니오, 1-예)';
COMMENT ON COLUMN ai_command_record.requires_confirmation IS '확인 필요 여부(0-아니오, 1-예)';
COMMENT ON COLUMN ai_command_record.user_confirmed IS '사용자 확인 여부(0-아니오, 1-예)';
COMMENT ON COLUMN ai_command_record.idempotency_key IS '멱등성 토큰';
COMMENT ON COLUMN ai_command_record.execution_time IS '실행 소요 시간(밀리초)';
COMMENT ON COLUMN ai_command_record.ip_address IS 'IP 주소';
COMMENT ON COLUMN ai_command_record.user_agent IS '사용자 에이전트';
COMMENT ON COLUMN ai_command_record.current_route IS '현재 페이지 라우트';
COMMENT ON COLUMN ai_command_record.create_time IS '생성 시간';
COMMENT ON COLUMN ai_command_record.update_time IS '수정 시간';
COMMENT ON COLUMN ai_command_record.remark IS '비고';

CREATE OR REPLACE TRIGGER trg_ai_command_record_id
BEFORE INSERT ON ai_command_record
FOR EACH ROW
WHEN (NEW.id IS NULL)
BEGIN
    SELECT seq_ai_command_record.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- ============================
-- 5. 초기 데이터 삽입
-- ============================

-- sys_dept 초기 데이터
INSERT INTO sys_dept (id, name, code, parent_id, tree_path, sort, status, create_by, create_time, update_by, update_time, is_deleted) VALUES (1, '유라이 기술', 'YOULAI', 0, '0', 1, 1, 1, SYSDATE, 1, SYSDATE, 0);
INSERT INTO sys_dept (id, name, code, parent_id, tree_path, sort, status, create_by, create_time, update_by, update_time, is_deleted) VALUES (2, '연구개발부', 'RD001', 1, '0,1', 1, 1, 2, SYSDATE, 2, SYSDATE, 0);
INSERT INTO sys_dept (id, name, code, parent_id, tree_path, sort, status, create_by, create_time, update_by, update_time, is_deleted) VALUES (3, '테스트부', 'QA001', 1, '0,1', 1, 1, 2, SYSDATE, 2, SYSDATE, 0);

-- sys_dict 초기 데이터
INSERT INTO sys_dict (id, dict_code, name, status, remark, create_time, create_by, update_time, update_by, is_deleted) VALUES (1, 'gender', '성별', 1, NULL, SYSDATE, 1, SYSDATE, 1, 0);
INSERT INTO sys_dict (id, dict_code, name, status, remark, create_time, create_by, update_time, update_by, is_deleted) VALUES (2, 'notice_type', '알림 유형', 1, NULL, SYSDATE, 1, SYSDATE, 1, 0);
INSERT INTO sys_dict (id, dict_code, name, status, remark, create_time, create_by, update_time, update_by, is_deleted) VALUES (3, 'notice_level', '알림 수준', 1, NULL, SYSDATE, 1, SYSDATE, 1, 0);

-- sys_dict_item 초기 데이터
INSERT INTO sys_dict_item (id, dict_code, value, label, tag_type, status, sort, remark, create_time, create_by, update_time, update_by) VALUES (1, 'gender', '1', '남성', 'primary', 1, 1, NULL, SYSDATE, 1, SYSDATE, 1);
INSERT INTO sys_dict_item (id, dict_code, value, label, tag_type, status, sort, remark, create_time, create_by, update_time, update_by) VALUES (2, 'gender', '2', '여성', 'danger', 1, 2, NULL, SYSDATE, 1, SYSDATE, 1);
INSERT INTO sys_dict_item (id, dict_code, value, label, tag_type, status, sort, remark, create_time, create_by, update_time, update_by) VALUES (3, 'gender', '0', '비공개', 'info', 1, 3, NULL, SYSDATE, 1, SYSDATE, 1);
INSERT INTO sys_dict_item (id, dict_code, value, label, tag_type, status, sort, remark, create_time, create_by, update_time, update_by) VALUES (4, 'notice_type', '1', '시스템 업그레이드', 'success', 1, 1, '', SYSDATE, 1, SYSDATE, 1);
INSERT INTO sys_dict_item (id, dict_code, value, label, tag_type, status, sort, remark, create_time, create_by, update_time, update_by) VALUES (5, 'notice_type', '2', '시스템 유지보수', 'primary', 1, 2, '', SYSDATE, 1, SYSDATE, 1);
INSERT INTO sys_dict_item (id, dict_code, value, label, tag_type, status, sort, remark, create_time, create_by, update_time, update_by) VALUES (6, 'notice_type', '3', '보안 경고', 'danger', 1, 3, '', SYSDATE, 1, SYSDATE, 1);
INSERT INTO sys_dict_item (id, dict_code, value, label, tag_type, status, sort, remark, create_time, create_by, update_time, update_by) VALUES (7, 'notice_type', '4', '휴가 알림', 'success', 1, 4, '', SYSDATE, 1, SYSDATE, 1);
INSERT INTO sys_dict_item (id, dict_code, value, label, tag_type, status, sort, remark, create_time, create_by, update_time, update_by) VALUES (8, 'notice_type', '5', '회사 뉴스', 'primary', 1, 5, '', SYSDATE, 1, SYSDATE, 1);
INSERT INTO sys_dict_item (id, dict_code, value, label, tag_type, status, sort, remark, create_time, create_by, update_time, update_by) VALUES (9, 'notice_type', '99', '기타', 'info', 1, 99, '', SYSDATE, 1, SYSDATE, 1);
INSERT INTO sys_dict_item (id, dict_code, value, label, tag_type, status, sort, remark, create_time, create_by, update_time, update_by) VALUES (10, 'notice_level', 'L', '낮음', 'info', 1, 1, '', SYSDATE, 1, SYSDATE, 1);
INSERT INTO sys_dict_item (id, dict_code, value, label, tag_type, status, sort, remark, create_time, create_by, update_time, update_by) VALUES (11, 'notice_level', 'M', '중간', 'warning', 1, 2, '', SYSDATE, 1, SYSDATE, 1);
INSERT INTO sys_dict_item (id, dict_code, value, label, tag_type, status, sort, remark, create_time, create_by, update_time, update_by) VALUES (12, 'notice_level', 'H', '높음', 'danger', 1, 3, '', SYSDATE, 1, SYSDATE, 1);

-- sys_menu 초기 데이터
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (1, 0, '0', '시스템 관리', 2, '', '/system', 'Layout', NULL, NULL, NULL, 1, 1, 'system', '/system/user', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (2, 1, '0,1', '사용자 관리', 1, 'User', 'user', 'system/user/index', NULL, NULL, 1, 1, 1, 'el-icon-User', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (3, 1, '0,1', '역할 관리', 1, 'Role', 'role', 'system/role/index', NULL, NULL, 1, 1, 2, 'role', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (4, 1, '0,1', '메뉴 관리', 1, 'SysMenu', 'menu', 'system/menu/index', NULL, NULL, 1, 1, 3, 'menu', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (5, 1, '0,1', '부서 관리', 1, 'Dept', 'dept', 'system/dept/index', NULL, NULL, 1, 1, 4, 'tree', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (6, 1, '0,1', '사전 관리', 1, 'Dict', 'dict', 'system/dict/index', NULL, NULL, 1, 1, 5, 'dict', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (20, 0, '0', '다단계 메뉴', 2, NULL, '/multi-level', 'Layout', NULL, 1, NULL, 1, 9, 'cascader', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (21, 20, '0,20', '메뉴 1단계', 2, NULL, 'multi-level1', 'Layout', NULL, 1, NULL, 1, 1, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (22, 21, '0,20,21', '메뉴 2단계', 2, NULL, 'multi-level2', 'Layout', NULL, 0, NULL, 1, 1, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (23, 22, '0,20,21,22', '메뉴 3단계-1', 1, NULL, 'multi-level3-1', 'demo/multi-level/children/children/level3-1', NULL, 0, 1, 1, 1, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (24, 22, '0,20,21,22', '메뉴 3단계-2', 1, NULL, 'multi-level3-2', 'demo/multi-level/children/children/level3-2', NULL, 0, 1, 1, 2, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (26, 0, '0', '플랫폼 문서', 2, '', '/doc', 'Layout', NULL, NULL, NULL, 1, 8, 'document', 'https://juejin.cn/post/7228990409909108793', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (30, 26, '0,26', '플랫폼 문서(외부링크)', 3, NULL, 'https://juejin.cn/post/7228990409909108793', '', NULL, NULL, NULL, 1, 2, 'document', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (31, 2, '0,1,2', '사용자 추가', 4, NULL, '', NULL, 'sys:user:add', NULL, NULL, 1, 1, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (32, 2, '0,1,2', '사용자 편집', 4, NULL, '', NULL, 'sys:user:edit', NULL, NULL, 1, 2, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (33, 2, '0,1,2', '사용자 삭제', 4, NULL, '', NULL, 'sys:user:delete', NULL, NULL, 1, 3, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (36, 0, '0', '컴포넌트 캡슐화', 2, NULL, '/component', 'Layout', NULL, NULL, NULL, 1, 10, 'menu', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (37, 36, '0,36', '리치 텍스트 에디터', 1, 'WangEditor', 'wang-editor', 'demo/wang-editor', NULL, NULL, 1, 1, 2, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (38, 36, '0,36', '이미지 업로드', 1, 'Upload', 'upload', 'demo/upload', NULL, NULL, 1, 1, 3, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (39, 36, '0,36', '아이콘 선택기', 1, 'IconSelect', 'icon-select', 'demo/icon-select', NULL, NULL, 1, 1, 4, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (40, 0, '0', 'API 문서', 2, NULL, '/api', 'Layout', NULL, 1, NULL, 1, 7, 'api', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (41, 40, '0,40', 'Apifox', 1, 'Apifox', 'apifox', 'demo/api/apifox', NULL, NULL, 1, 1, 1, 'api', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (70, 3, '0,1,3', '역할 추가', 4, NULL, '', NULL, 'sys:role:add', NULL, NULL, 1, 2, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (71, 3, '0,1,3', '역할 편집', 4, NULL, '', NULL, 'sys:role:edit', NULL, NULL, 1, 3, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (72, 3, '0,1,3', '역할 삭제', 4, NULL, '', NULL, 'sys:role:delete', NULL, NULL, 1, 4, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (73, 4, '0,1,4', '메뉴 추가', 4, NULL, '', NULL, 'sys:menu:add', NULL, NULL, 1, 1, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (74, 4, '0,1,4', '메뉴 편집', 4, NULL, '', NULL, 'sys:menu:edit', NULL, NULL, 1, 3, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (75, 4, '0,1,4', '메뉴 삭제', 4, NULL, '', NULL, 'sys:menu:delete', NULL, NULL, 1, 3, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (76, 5, '0,1,5', '부서 추가', 4, NULL, '', NULL, 'sys:dept:add', NULL, NULL, 1, 1, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (77, 5, '0,1,5', '부서 편집', 4, NULL, '', NULL, 'sys:dept:edit', NULL, NULL, 1, 2, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (78, 5, '0,1,5', '부서 삭제', 4, NULL, '', NULL, 'sys:dept:delete', NULL, NULL, 1, 3, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (79, 6, '0,1,6', '사전 추가', 4, NULL, '', NULL, 'sys:dict:add', NULL, NULL, 1, 1, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (81, 6, '0,1,6', '사전 편집', 4, NULL, '', NULL, 'sys:dict:edit', NULL, NULL, 1, 2, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (84, 6, '0,1,6', '사전 삭제', 4, NULL, '', NULL, 'sys:dict:delete', NULL, NULL, 1, 3, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (88, 2, '0,1,2', '비밀번호 재설정', 4, NULL, '', NULL, 'sys:user:reset-password', NULL, NULL, 1, 4, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (89, 0, '0', '기능 데모', 2, NULL, '/function', 'Layout', NULL, NULL, NULL, 1, 12, 'menu', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (90, 89, '0,89', 'Websocket', 1, 'WebSocket', '/function/websocket', 'demo/websocket', NULL, NULL, 1, 1, 3, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (95, 36, '0,36', '사전 컴포넌트', 1, 'DictDemo', 'dict-demo', 'demo/dictionary', NULL, NULL, 1, 1, 4, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (97, 89, '0,89', 'Icons', 1, 'IconDemo', 'icon-demo', 'demo/icons', NULL, NULL, 1, 1, 2, 'el-icon-Notification', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (102, 26, '0,26', 'document', 3, NULL, 'internal-doc', 'demo/internal-doc', NULL, NULL, NULL, 1, 1, 'document', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (105, 2, '0,1,2', '사용자 조회', 4, NULL, '', NULL, 'sys:user:query', 0, 0, 1, 0, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (106, 2, '0,1,2', '사용자 가져오기', 4, NULL, '', NULL, 'sys:user:import', NULL, NULL, 1, 5, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (107, 2, '0,1,2', '사용자 내보내기', 4, NULL, '', NULL, 'sys:user:export', NULL, NULL, 1, 6, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (108, 36, '0,36', 'CRUD', 1, 'Curd', 'curd', 'demo/curd/index', NULL, NULL, 1, 1, 0, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (109, 36, '0,36', '목록 선택기', 1, 'TableSelect', 'table-select', 'demo/table-select/index', NULL, NULL, 1, 1, 1, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (110, 0, '0', '라우트 파라미터', 2, NULL, '/route-param', 'Layout', NULL, 1, 1, 1, 11, 'el-icon-ElementPlus', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (111, 110, '0,110', '파라미터(type=1)', 1, 'RouteParamType1', 'route-param-type1', 'demo/route-param', NULL, 0, 1, 1, 1, 'el-icon-Star', NULL, SYSDATE, SYSDATE, '{"type": "1"}');
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (112, 110, '0,110', '파라미터(type=2)', 1, 'RouteParamType2', 'route-param-type2', 'demo/route-param', NULL, 0, 1, 1, 2, 'el-icon-StarFilled', NULL, SYSDATE, SYSDATE, '{"type": "2"}');
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (117, 1, '0,1', '시스템 로그', 1, 'Log', 'log', 'system/log/index', NULL, 0, 1, 1, 6, 'document', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (118, 0, '0', '시스템 도구', 2, NULL, '/codegen', 'Layout', NULL, 0, 1, 1, 2, 'menu', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (119, 118, '0,118', '코드 생성', 1, 'Codegen', 'codegen', 'codegen/index', NULL, 0, 1, 1, 1, 'code', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (120, 1, '0,1', '시스템 설정', 1, 'Config', 'config', 'system/config/index', NULL, 0, 1, 1, 7, 'setting', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (121, 120, '0,1,120', '시스템 설정 조회', 4, NULL, '', NULL, 'sys:config:query', 0, 1, 1, 1, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (122, 120, '0,1,120', '시스템 설정 추가', 4, NULL, '', NULL, 'sys:config:add', 0, 1, 1, 2, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (123, 120, '0,1,120', '시스템 설정 수정', 4, NULL, '', NULL, 'sys:config:update', 0, 1, 1, 3, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (124, 120, '0,1,120', '시스템 설정 삭제', 4, NULL, '', NULL, 'sys:config:delete', 0, 1, 1, 4, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (125, 120, '0,1,120', '시스템 설정 새로고침', 4, NULL, '', NULL, 'sys:config:refresh', 0, 1, 1, 5, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (126, 1, '0,1', '공지사항', 1, 'Notice', 'notice', 'system/notice/index', NULL, NULL, NULL, 1, 9, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (127, 126, '0,1,126', '공지 조회', 4, NULL, '', NULL, 'sys:notice:query', NULL, NULL, 1, 1, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (128, 126, '0,1,126', '공지 추가', 4, NULL, '', NULL, 'sys:notice:add', NULL, NULL, 1, 2, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (129, 126, '0,1,126', '공지 편집', 4, NULL, '', NULL, 'sys:notice:edit', NULL, NULL, 1, 3, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (130, 126, '0,1,126', '공지 삭제', 4, NULL, '', NULL, 'sys:notice:delete', NULL, NULL, 1, 4, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (133, 126, '0,1,126', '공지 발행', 4, NULL, '', NULL, 'sys:notice:publish', 0, 1, 1, 5, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (134, 126, '0,1,126', '공지 철회', 4, NULL, '', NULL, 'sys:notice:revoke', 0, 1, 1, 6, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (135, 1, '0,1', '사전 항목', 1, 'DictItem', 'dict-item', 'system/dict/dict-item', NULL, 0, 1, 0, 6, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (136, 135, '0,1,135', '사전 항목 추가', 4, NULL, '', NULL, 'sys:dict-item:add', NULL, NULL, 1, 2, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (137, 135, '0,1,135', '사전 항목 편집', 4, NULL, '', NULL, 'sys:dict-item:edit', NULL, NULL, 1, 3, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (138, 135, '0,1,135', '사전 항목 삭제', 4, NULL, '', NULL, 'sys:dict-item:delete', NULL, NULL, 1, 4, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (139, 3, '0,1,3', '역할 조회', 4, NULL, '', NULL, 'sys:role:query', NULL, NULL, 1, 1, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (140, 4, '0,1,4', '메뉴 조회', 4, NULL, '', NULL, 'sys:menu:query', NULL, NULL, 1, 1, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (141, 5, '0,1,5', '부서 조회', 4, NULL, '', NULL, 'sys:dept:query', NULL, NULL, 1, 1, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (142, 6, '0,1,6', '사전 조회', 4, NULL, '', NULL, 'sys:dict:query', NULL, NULL, 1, 1, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (143, 135, '0,1,135', '사전 항목 조회', 4, NULL, '', NULL, 'sys:dict-item:query', NULL, NULL, 1, 1, '', NULL, SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (144, 26, '0,26', '백엔드 문서', 3, NULL, 'https://youlai.blog.csdn.net/article/details/145178880', '', NULL, NULL, NULL, 1, 3, 'document', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (145, 26, '0,26', '모바일 문서', 3, NULL, 'https://youlai.blog.csdn.net/article/details/143222890', '', NULL, NULL, NULL, 1, 4, 'document', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (146, 36, '0,36', '드래그 컴포넌트', 1, 'Drag', 'drag', 'demo/drag', NULL, NULL, NULL, 1, 5, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (147, 36, '0,36', '스크롤 텍스트', 1, 'TextScroll', 'text-scroll', 'demo/text-scroll', NULL, NULL, NULL, 1, 6, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (148, 89, '0,89', '사전 실시간 동기화', 1, 'DictSync', 'dict-sync', 'demo/dict-sync', NULL, NULL, NULL, 1, 3, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (149, 89, '0,89', 'VxeTable', 1, 'VxeTable', 'vxe-table', 'demo/vxe-table/index', NULL, NULL, 1, 1, 0, 'el-icon-MagicStick', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (150, 36, '0,36', '자동 테이블 작업 열', 1, 'AutoOperationColumn', 'operation-column', 'demo/auto-operation-column', NULL, NULL, 1, 1, 1, '', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (151, 89, '0,89', 'CRUD 단일 파일', 1, 'CurdSingle', 'curd-single', 'demo/curd-single', NULL, NULL, 1, 1, 7, 'el-icon-Reading', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (152, 0, '0', 'AI 도우미', 2, NULL, '/platform', 'Layout', NULL, NULL, NULL, 1, 13, 'platform', '', SYSDATE, SYSDATE, NULL);
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES (153, 152, '0,152', 'AI 명령 기록', 1, 'AiCommandRecord', 'command-record', 'ai/command-record/index', NULL, NULL, 1, 1, 1, 'document', NULL, SYSDATE, SYSDATE, NULL);

-- sys_role 초기 데이터
INSERT INTO sys_role (id, name, code, sort, status, data_scope, create_by, create_time, update_by, update_time, is_deleted) VALUES (1, '슈퍼 관리자', 'ROOT', 1, 1, 1, NULL, SYSDATE, NULL, SYSDATE, 0);
INSERT INTO sys_role (id, name, code, sort, status, data_scope, create_by, create_time, update_by, update_time, is_deleted) VALUES (2, '시스템 관리자', 'ADMIN', 2, 1, 1, NULL, SYSDATE, NULL, NULL, 0);
INSERT INTO sys_role (id, name, code, sort, status, data_scope, create_by, create_time, update_by, update_time, is_deleted) VALUES (3, '방문 게스트', 'GUEST', 3, 1, 3, NULL, SYSDATE, NULL, SYSDATE, 0);
INSERT INTO sys_role (id, name, code, sort, status, data_scope, create_by, create_time, update_by, update_time, is_deleted) VALUES (4, '시스템 관리자1', 'ADMIN1', 4, 1, 1, NULL, SYSDATE, NULL, NULL, 0);
INSERT INTO sys_role (id, name, code, sort, status, data_scope, create_by, create_time, update_by, update_time, is_deleted) VALUES (5, '시스템 관리자2', 'ADMIN2', 5, 1, 1, NULL, SYSDATE, NULL, NULL, 0);
INSERT INTO sys_role (id, name, code, sort, status, data_scope, create_by, create_time, update_by, update_time, is_deleted) VALUES (6, '시스템 관리자3', 'ADMIN3', 6, 1, 1, NULL, SYSDATE, NULL, NULL, 0);
INSERT INTO sys_role (id, name, code, sort, status, data_scope, create_by, create_time, update_by, update_time, is_deleted) VALUES (7, '시스템 관리자4', 'ADMIN4', 7, 1, 1, NULL, SYSDATE, NULL, NULL, 0);
INSERT INTO sys_role (id, name, code, sort, status, data_scope, create_by, create_time, update_by, update_time, is_deleted) VALUES (8, '시스템 관리자5', 'ADMIN5', 8, 1, 1, NULL, SYSDATE, NULL, NULL, 0);
INSERT INTO sys_role (id, name, code, sort, status, data_scope, create_by, create_time, update_by, update_time, is_deleted) VALUES (9, '시스템 관리자6', 'ADMIN6', 9, 1, 1, NULL, SYSDATE, NULL, NULL, 0);
INSERT INTO sys_role (id, name, code, sort, status, data_scope, create_by, create_time, update_by, update_time, is_deleted) VALUES (10, '시스템 관리자7', 'ADMIN7', 10, 1, 1, NULL, SYSDATE, NULL, NULL, 0);
INSERT INTO sys_role (id, name, code, sort, status, data_scope, create_by, create_time, update_by, update_time, is_deleted) VALUES (11, '시스템 관리자8', 'ADMIN8', 11, 1, 1, NULL, SYSDATE, NULL, NULL, 0);
INSERT INTO sys_role (id, name, code, sort, status, data_scope, create_by, create_time, update_by, update_time, is_deleted) VALUES (12, '시스템 관리자9', 'ADMIN9', 12, 1, 1, NULL, SYSDATE, NULL, NULL, 0);

-- sys_role_menu 초기 데이터
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 2);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 3);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 4);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 5);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 6);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 20);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 21);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 22);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 23);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 24);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 26);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 30);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 31);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 32);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 33);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 36);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 37);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 38);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 39);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 40);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 41);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 70);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 71);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 72);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 73);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 74);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 75);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 76);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 77);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 78);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 79);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 81);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 84);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 85);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 86);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 87);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 88);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 89);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 90);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 91);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 95);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 97);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 102);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 105);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 106);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 107);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 108);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 109);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 110);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 111);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 112);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 114);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 115);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 116);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 117);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 118);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 119);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 120);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 121);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 122);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 123);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 124);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 125);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 126);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 127);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 128);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 129);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 130);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 131);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 132);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 133);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 134);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 135);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 136);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 137);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 138);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 139);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 140);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 141);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 142);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 143);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 144);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 145);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 146);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 147);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 148);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 149);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 150);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 151);

-- sys_user 초기 데이터
INSERT INTO sys_user (id, username, nickname, gender, password, dept_id, avatar, mobile, status, email, create_time, create_by, update_time, update_by, is_deleted, openid) VALUES (1, 'root', '유라이 기술', 0, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', NULL, 'https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif', '18812345677', 1, 'youlaitech@163.com', SYSDATE, NULL, SYSDATE, NULL, 0, NULL);
INSERT INTO sys_user (id, username, nickname, gender, password, dept_id, avatar, mobile, status, email, create_time, create_by, update_time, update_by, is_deleted, openid) VALUES (2, 'admin', '시스템 관리자', 1, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', 1, 'https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif', '18812345678', 1, 'youlaitech@163.com', SYSDATE, NULL, SYSDATE, NULL, 0, NULL);
INSERT INTO sys_user (id, username, nickname, gender, password, dept_id, avatar, mobile, status, email, create_time, create_by, update_time, update_by, is_deleted, openid) VALUES (3, 'test', '테스트 사용자', 1, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', 3, 'https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif', '18812345679', 1, 'youlaitech@163.com', SYSDATE, NULL, SYSDATE, NULL, 0, NULL);

-- sys_user_role 초기 데이터
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO sys_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO sys_user_role (user_id, role_id) VALUES (3, 3);

-- sys_config 초기 데이터
INSERT INTO sys_config (id, config_name, config_key, config_value, remark, create_time, create_by, update_time, update_by, is_deleted) VALUES (1, '시스템 제한 QPS', 'IP_QPS_THRESHOLD_LIMIT', '10', '단일 IP 요청의 최대 초당 쿼리 수(QPS) 임계값 Key', SYSDATE, 1, NULL, NULL, 0);

-- sys_notice 초기 데이터
INSERT INTO sys_notice (id, title, content, NOTICE_TYPE, NOTICE_LEVEL, target_type, target_user_ids, publisher_id, publish_status, publish_time, revoke_time, create_by, create_time, update_by, update_time, is_deleted) VALUES (1, 'v2.12.0 시스템 로그, 접근 추세 통계 기능 추가.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 1, 'L', 1, '2', 1, 1, SYSDATE, SYSDATE, 2, SYSDATE, 1, SYSDATE, 0);
INSERT INTO sys_notice (id, title, content, NOTICE_TYPE, NOTICE_LEVEL, target_type, target_user_ids, publisher_id, publish_status, publish_time, revoke_time, create_by, create_time, update_by, update_time, is_deleted) VALUES (2, 'v2.13.0 메뉴 검색 추가.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 1, 'L', 1, '2', 1, 1, SYSDATE, SYSDATE, 2, SYSDATE, 1, SYSDATE, 0);
INSERT INTO sys_notice (id, title, content, NOTICE_TYPE, NOTICE_LEVEL, target_type, target_user_ids, publisher_id, publish_status, publish_time, revoke_time, create_by, create_time, update_by, update_time, is_deleted) VALUES (3, 'v2.14.0 개인 센터 추가.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 1, 'L', 1, '2', 2, 1, SYSDATE, SYSDATE, 2, SYSDATE, 2, SYSDATE, 0);
INSERT INTO sys_notice (id, title, content, NOTICE_TYPE, NOTICE_LEVEL, target_type, target_user_ids, publisher_id, publish_status, publish_time, revoke_time, create_by, create_time, update_by, update_time, is_deleted) VALUES (4, 'v2.15.0 로그인 페이지 개편.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 1, 'L', 1, '2', 2, 1, SYSDATE, SYSDATE, 2, SYSDATE, 2, SYSDATE, 0);
INSERT INTO sys_notice (id, title, content, NOTICE_TYPE, NOTICE_LEVEL, target_type, target_user_ids, publisher_id, publish_status, publish_time, revoke_time, create_by, create_time, update_by, update_time, is_deleted) VALUES (5, 'v2.16.0 공지사항, 사전 번역 컴포넌트.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 1, 'L', 1, '2', 2, 1, SYSDATE, SYSDATE, 2, SYSDATE, 2, SYSDATE, 0);
INSERT INTO sys_notice (id, title, content, NOTICE_TYPE, NOTICE_LEVEL, target_type, target_user_ids, publisher_id, publish_status, publish_time, revoke_time, create_by, create_time, update_by, update_time, is_deleted) VALUES (6, '시스템은 이번 주 토요일 새벽 2시에 유지보수를 진행하며, 예상 유지보수 시간은 2시간입니다.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 2, 'H', 1, '2', 2, 1, SYSDATE, SYSDATE, 2, SYSDATE, 2, SYSDATE, 0);
INSERT INTO sys_notice (id, title, content, NOTICE_TYPE, NOTICE_LEVEL, target_type, target_user_ids, publisher_id, publish_status, publish_time, revoke_time, create_by, create_time, update_by, update_time, is_deleted) VALUES (7, '최근 피싱 이메일이 발견되었으니, 낯선 링크를 클릭하지 마시기 바랍니다.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 3, 'L', 1, '2', 2, 1, SYSDATE, SYSDATE, 2, SYSDATE, 2, SYSDATE, 0);
INSERT INTO sys_notice (id, title, content, NOTICE_TYPE, NOTICE_LEVEL, target_type, target_user_ids, publisher_id, publish_status, publish_time, revoke_time, create_by, create_time, update_by, update_time, is_deleted) VALUES (8, '국경절 연휴는 10월 1일부터 10월 7일까지 7일간입니다.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 4, 'L', 1, '2', 2, 1, SYSDATE, SYSDATE, 2, SYSDATE, 2, SYSDATE, 0);
INSERT INTO sys_notice (id, title, content, NOTICE_TYPE, NOTICE_LEVEL, target_type, target_user_ids, publisher_id, publish_status, publish_time, revoke_time, create_by, create_time, update_by, update_time, is_deleted) VALUES (9, '회사는 10월 15일에 신제품 발표회를 개최합니다. 많은 기대 부탁드립니다.', '회사는 10월 15일에 신제품 발표회를 개최합니다. 많은 기대 부탁드립니다.', 5, 'H', 1, '2', 2, 1, SYSDATE, SYSDATE, 2, SYSDATE, 2, SYSDATE, 0);
INSERT INTO sys_notice (id, title, content, NOTICE_TYPE, NOTICE_LEVEL, target_type, target_user_ids, publisher_id, publish_status, publish_time, revoke_time, create_by, create_time, update_by, update_time, is_deleted) VALUES (10, 'v2.16.1 버전 출시.', 'v2.16.1 버전은 WebSocket 중복 연결로 인한 백엔드 스레드 차단 문제를 수정하고, 공지사항을 최적화했습니다.', 1, 'M', 1, '2', 2, 1, SYSDATE, SYSDATE, 2, SYSDATE, 2, SYSDATE, 0);

-- sys_user_notice 초기 데이터
INSERT INTO sys_user_notice (id, notice_id, user_id, is_read, read_time, create_time, update_time, is_deleted) VALUES (1, 1, 2, 1, NULL, SYSDATE, SYSDATE, 0);
INSERT INTO sys_user_notice (id, notice_id, user_id, is_read, read_time, create_time, update_time, is_deleted) VALUES (2, 2, 2, 1, NULL, SYSDATE, SYSDATE, 0);
INSERT INTO sys_user_notice (id, notice_id, user_id, is_read, read_time, create_time, update_time, is_deleted) VALUES (3, 3, 2, 1, NULL, SYSDATE, SYSDATE, 0);
INSERT INTO sys_user_notice (id, notice_id, user_id, is_read, read_time, create_time, update_time, is_deleted) VALUES (4, 4, 2, 1, NULL, SYSDATE, SYSDATE, 0);
INSERT INTO sys_user_notice (id, notice_id, user_id, is_read, read_time, create_time, update_time, is_deleted) VALUES (5, 5, 2, 1, NULL, SYSDATE, SYSDATE, 0);
INSERT INTO sys_user_notice (id, notice_id, user_id, is_read, read_time, create_time, update_time, is_deleted) VALUES (6, 6, 2, 1, NULL, SYSDATE, SYSDATE, 0);
INSERT INTO sys_user_notice (id, notice_id, user_id, is_read, read_time, create_time, update_time, is_deleted) VALUES (7, 7, 2, 1, NULL, SYSDATE, SYSDATE, 0);
INSERT INTO sys_user_notice (id, notice_id, user_id, is_read, read_time, create_time, update_time, is_deleted) VALUES (8, 8, 2, 1, NULL, SYSDATE, SYSDATE, 0);
INSERT INTO sys_user_notice (id, notice_id, user_id, is_read, read_time, create_time, update_time, is_deleted) VALUES (9, 9, 2, 1, NULL, SYSDATE, SYSDATE, 0);
INSERT INTO sys_user_notice (id, notice_id, user_id, is_read, read_time, create_time, update_time, is_deleted) VALUES (10, 10, 2, 1, NULL, SYSDATE, SYSDATE, 0);

-- ============================
-- 6. 커밋
-- ============================
COMMIT;
