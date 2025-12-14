
# YouLai_Boot 데이터베이스(MySQL 5.7 ~ MySQL 8.x)
# Copyright (c) 2021-present, youlai.tech


-- ----------------------------
-- 1. 데이터베이스 생성
-- ----------------------------
CREATE DATABASE IF NOT EXISTS youlai_boot CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;


-- ----------------------------
-- 2. 테이블 생성 && 데이터 초기화
-- ----------------------------
use youlai_boot;

SET NAMES utf8mb4;  # 문자 집합 설정
SET FOREIGN_KEY_CHECKS = 0; # 외래 키 검사 비활성화, 가져오기 속도 향상

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '기본키',
                             `name` varchar(100) NOT NULL COMMENT '부서 이름',
                             `code` varchar(100) NOT NULL COMMENT '부서 코드',
                             `parent_id` bigint DEFAULT 0 COMMENT '부모 노드 ID',
                             `tree_path` varchar(255) NOT NULL COMMENT '부모 노드 ID 경로',
                             `sort` smallint DEFAULT 0 COMMENT '표시 순서',
                             `status` tinyint DEFAULT 1 COMMENT '상태(1-정상 0-비활성)',
                             `create_by` bigint NULL COMMENT '생성자 ID',
                             `create_time` datetime NULL COMMENT '생성 시간',
                             `update_by` bigint NULL COMMENT '수정자 ID',
                             `update_time` datetime NULL COMMENT '수정 시간',
                             `is_deleted` tinyint DEFAULT 0 COMMENT '논리 삭제 플래그(1-삭제됨 0-삭제안됨)',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE COMMENT '부서 코드 고유 인덱스'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '부서 테이블';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (1, '유라이 기술', 'YOULAI', 0, '0', 1, 1, 1, NULL, 1, now(), 0);
INSERT INTO `sys_dept` VALUES (2, '연구개발부', 'RD001', 1, '0,1', 1, 1, 2, NULL, 2, now(), 0);
INSERT INTO `sys_dept` VALUES (3, '테스트부', 'QA001', 1, '0,1', 1, 1, 2, NULL, 2, now(), 0);

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '기본키',
                            `dict_code` varchar(50) COMMENT '유형 코드',
                            `name` varchar(50) COMMENT '유형 이름',
                            `status` tinyint(1) DEFAULT '0' COMMENT '상태(0:정상;1:비활성)',
                            `remark` varchar(255) COMMENT '비고',
                            `create_time` datetime COMMENT '생성 시간',
                            `create_by` bigint COMMENT '생성자 ID',
                            `update_time` datetime COMMENT '수정 시간',
                            `update_by` bigint COMMENT '수정자 ID',
                            `is_deleted` tinyint DEFAULT '0' COMMENT '삭제 여부(1-삭제됨，0-삭제안됨)',
                            PRIMARY KEY (`id`) USING BTREE,
                            KEY `idx_dict_code` (`dict_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='사전 테이블';
-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1, 'gender', '성별', 1, NULL, now() , 1,now(), 1,0);
INSERT INTO `sys_dict` VALUES (2, 'notice_type', '알림 유형', 1, NULL, now(), 1,now(), 1,0);
INSERT INTO `sys_dict` VALUES (3, 'notice_level', '알림 수준', 1, NULL, now(), 1,now(), 1,0);


-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '기본키',
                                 `dict_code` varchar(50) COMMENT '연관 사전 코드, sys_dict 테이블의 dict_code와 대응',
                                 `value` varchar(50) COMMENT '사전 항목 값',
                                 `label` varchar(100) COMMENT '사전 항목 레이블',
                                 `tag_type` varchar(50) COMMENT '태그 유형, 프론트엔드 스타일 표시용(예: success, warning 등)',
                                 `status` tinyint DEFAULT '0' COMMENT '상태（1-정상，0-비활성）',
                                 `sort` int DEFAULT '0' COMMENT '정렬',
                                 `remark` varchar(255) COMMENT '비고',
                                 `create_time` datetime COMMENT '생성 시간',
                                 `create_by` bigint COMMENT '생성자 ID',
                                 `update_time` datetime COMMENT '수정 시간',
                                 `update_by` bigint COMMENT '수정자 ID',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='사전 항목 테이블';

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO `sys_dict_item` VALUES (1, 'gender', '1', '남성', 'primary', 1, 1, NULL, now(), 1,now(),1);
INSERT INTO `sys_dict_item` VALUES (2, 'gender', '2', '여성', 'danger', 1, 2, NULL, now(), 1,now(),1);
INSERT INTO `sys_dict_item` VALUES (3, 'gender', '0', '비공개', 'info', 1, 3, NULL, now(), 1,now(),1);
INSERT INTO `sys_dict_item` VALUES (4, 'notice_type', '1', '시스템 업그레이드', 'success', 1, 1, '', now(), 1,now(),1);
INSERT INTO `sys_dict_item` VALUES (5, 'notice_type', '2', '시스템 유지보수', 'primary', 1, 2, '', now(), 1,now(),1);
INSERT INTO `sys_dict_item` VALUES (6, 'notice_type', '3', '보안 경고', 'danger', 1, 3, '', now(), 1,now(),1);
INSERT INTO `sys_dict_item` VALUES (7, 'notice_type', '4', '휴가 알림', 'success', 1, 4, '', now(), 1,now(),1);
INSERT INTO `sys_dict_item` VALUES (8, 'notice_type', '5', '회사 뉴스', 'primary', 1, 5, '', now(), 1,now(),1);
INSERT INTO `sys_dict_item` VALUES (9, 'notice_type', '99', '기타', 'info', 1, 99, '', now(), 1,now(),1);
INSERT INTO `sys_dict_item` VALUES (10, 'notice_level', 'L', '낮음', 'info', 1, 1, '', now(), 1,now(),1);
INSERT INTO `sys_dict_item` VALUES (11, 'notice_level', 'M', '중간', 'warning', 1, 2, '', now(), 1,now(),1);
INSERT INTO `sys_dict_item` VALUES (12, 'notice_level', 'H', '높음', 'danger', 1, 3, '', now(), 1,now(),1);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                             `parent_id` bigint NOT NULL COMMENT '부모 메뉴 ID',
                             `tree_path` varchar(255) COMMENT '부모 노드 ID 경로',
                             `name` varchar(64) NOT NULL COMMENT '메뉴 이름',
                             `type` tinyint NOT NULL COMMENT '메뉴 유형（1-메뉴 2-디렉토리 3-외부링크 4-버튼）',
                             `route_name` varchar(255) COMMENT '라우트 이름（Vue Router에서 라우트 명명용）',
                             `route_path` varchar(128) COMMENT '라우트 경로（Vue Router에서 정의된 URL 경로）',
                             `component` varchar(128) COMMENT '컴포넌트 경로（컴포넌트 페이지 전체 경로, src/views/ 기준, 확장자 .vue 생략）',
                             `perm` varchar(128) COMMENT '【버튼】권한 식별자',
                             `always_show` tinyint DEFAULT 0 COMMENT '【디렉토리】자식 라우트가 하나일 때도 항상 표시（1-예 0-아니오）',
                             `keep_alive` tinyint DEFAULT 0 COMMENT '【메뉴】페이지 캐시 활성화（1-예 0-아니오）',
                             `visible` tinyint(1) DEFAULT 1 COMMENT '표시 상태（1-표시 0-숨김）',
                             `sort` int DEFAULT 0 COMMENT '정렬',
                             `icon` varchar(64) COMMENT '메뉴 아이콘',
                             `redirect` varchar(128) COMMENT '리다이렉트 경로',
                             `create_time` datetime NULL COMMENT '생성 시간',
                             `update_time` datetime NULL COMMENT '수정 시간',
                             `params` varchar(255) NULL COMMENT '라우트 파라미터',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '메뉴 관리';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, '0', '시스템 관리', 2, '', '/system', 'Layout', NULL, NULL, NULL, 1, 1, 'system', '/system/user', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (2, 1, '0,1', '사용자 관리', 1, 'User', 'user', 'system/user/index', NULL, NULL, 1, 1, 1, 'el-icon-User', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (3, 1, '0,1', '역할 관리', 1, 'Role', 'role', 'system/role/index', NULL, NULL, 1, 1, 2, 'role', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (4, 1, '0,1', '메뉴 관리', 1, 'SysMenu', 'menu', 'system/menu/index', NULL, NULL, 1, 1, 3, 'menu', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (5, 1, '0,1', '부서 관리', 1, 'Dept', 'dept', 'system/dept/index', NULL, NULL, 1, 1, 4, 'tree', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (6, 1, '0,1', '사전 관리', 1, 'Dict', 'dict', 'system/dict/index', NULL, NULL, 1, 1, 5, 'dict', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (20, 0, '0', '다단계 메뉴', 2, NULL, '/multi-level', 'Layout', NULL, 1, NULL, 1, 9, 'cascader', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (21, 20, '0,20', '메뉴 1단계', 2, NULL, 'multi-level1', 'Layout', NULL, 1, NULL, 1, 1, '', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (22, 21, '0,20,21', '메뉴 2단계', 2, NULL, 'multi-level2', 'Layout', NULL, 0, NULL, 1, 1, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (23, 22, '0,20,21,22', '메뉴 3단계-1', 1, NULL, 'multi-level3-1', 'demo/multi-level/children/children/level3-1', NULL, 0, 1, 1, 1, '', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (24, 22, '0,20,21,22', '메뉴 3단계-2', 1, NULL, 'multi-level3-2', 'demo/multi-level/children/children/level3-2', NULL, 0, 1, 1, 2, '', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (26, 0, '0', '플랫폼 문서', 2, '', '/doc', 'Layout', NULL, NULL, NULL, 1, 8, 'document', 'https://juejin.cn/post/7228990409909108793', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (30, 26, '0,26', '플랫폼 문서(외부링크)', 3, NULL, 'https://juejin.cn/post/7228990409909108793', '', NULL, NULL, NULL, 1, 2, 'document', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (31, 2, '0,1,2', '사용자 추가', 4, NULL, '', NULL, 'sys:user:add', NULL, NULL, 1, 1, '', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (32, 2, '0,1,2', '사용자 편집', 4, NULL, '', NULL, 'sys:user:edit', NULL, NULL, 1, 2, '', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (33, 2, '0,1,2', '사용자 삭제', 4, NULL, '', NULL, 'sys:user:delete', NULL, NULL, 1, 3, '', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (36, 0, '0', '컴포넌트 캡슐화', 2, NULL, '/component', 'Layout', NULL, NULL, NULL, 1, 10, 'menu', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (37, 36, '0,36', '리치 텍스트 에디터', 1, 'WangEditor', 'wang-editor', 'demo/wang-editor', NULL, NULL, 1, 1, 2, '', '', NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (38, 36, '0,36', '이미지 업로드', 1, 'Upload', 'upload', 'demo/upload', NULL, NULL, 1, 1, 3, '', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (39, 36, '0,36', '아이콘 선택기', 1, 'IconSelect', 'icon-select', 'demo/icon-select', NULL, NULL, 1, 1, 4, '', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (40, 0, '0', 'API 문서', 2, NULL, '/api', 'Layout', NULL, 1, NULL, 1, 7, 'api', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (41, 40, '0,40', 'Apifox', 1, 'Apifox', 'apifox', 'demo/api/apifox', NULL, NULL, 1, 1, 1, 'api', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (70, 3, '0,1,3', '역할 추가', 4, NULL, '', NULL, 'sys:role:add', NULL, NULL, 1, 2, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (71, 3, '0,1,3', '역할 편집', 4, NULL, '', NULL, 'sys:role:edit', NULL, NULL, 1, 3, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (72, 3, '0,1,3', '역할 삭제', 4, NULL, '', NULL, 'sys:role:delete', NULL, NULL, 1, 4, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (73, 4, '0,1,4', '메뉴 추가', 4, NULL, '', NULL, 'sys:menu:add', NULL, NULL, 1, 1, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (74, 4, '0,1,4', '메뉴 편집', 4, NULL, '', NULL, 'sys:menu:edit', NULL, NULL, 1, 3, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (75, 4, '0,1,4', '메뉴 삭제', 4, NULL, '', NULL, 'sys:menu:delete', NULL, NULL, 1, 3, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (76, 5, '0,1,5', '부서 추가', 4, NULL, '', NULL, 'sys:dept:add', NULL, NULL, 1, 1, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (77, 5, '0,1,5', '부서 편집', 4, NULL, '', NULL, 'sys:dept:edit', NULL, NULL, 1, 2, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (78, 5, '0,1,5', '부서 삭제', 4, NULL, '', NULL, 'sys:dept:delete', NULL, NULL, 1, 3, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (79, 6, '0,1,6', '사전 추가', 4, NULL, '', NULL, 'sys:dict:add', NULL, NULL, 1, 1, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (81, 6, '0,1,6', '사전 편집', 4, NULL, '', NULL, 'sys:dict:edit', NULL, NULL, 1, 2, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (84, 6, '0,1,6', '사전 삭제', 4, NULL, '', NULL, 'sys:dict:delete', NULL, NULL, 1, 3, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (88, 2, '0,1,2', '비밀번호 재설정', 4, NULL, '', NULL, 'sys:user:reset-password', NULL, NULL, 1, 4, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (89, 0, '0', '기능 데모', 2, NULL, '/function', 'Layout', NULL, NULL, NULL, 1, 12, 'menu', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (90, 89, '0,89', 'Websocket', 1, 'WebSocket', '/function/websocket', 'demo/websocket', NULL, NULL, 1, 1, 3, '', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (95, 36, '0,36', '사전 컴포넌트', 1, 'DictDemo', 'dict-demo', 'demo/dictionary', NULL, NULL, 1, 1, 4, '', '',  now(),  now(), NULL);
INSERT INTO `sys_menu` VALUES (97, 89, '0,89', 'Icons', 1, 'IconDemo', 'icon-demo', 'demo/icons', NULL, NULL, 1, 1, 2, 'el-icon-Notification', '',  now(),  now(), NULL);
INSERT INTO `sys_menu` VALUES (102, 26, '0,26', 'document', 3, NULL, 'internal-doc', 'demo/internal-doc', NULL, NULL, NULL, 1, 1, 'document', '',  now(),  now(), NULL);
INSERT INTO `sys_menu` VALUES (105, 2, '0,1,2', '사용자 조회', 4, NULL, '', NULL, 'sys:user:query', 0, 0, 1, 0, '', NULL,  now(),  now(), NULL);
INSERT INTO `sys_menu` VALUES (106, 2, '0,1,2', '사용자 가져오기', 4, NULL, '', NULL, 'sys:user:import', NULL, NULL, 1, 5, '', NULL,  now(),  now(), NULL);
INSERT INTO `sys_menu` VALUES (107, 2, '0,1,2', '사용자 내보내기', 4, NULL, '', NULL, 'sys:user:export', NULL, NULL, 1, 6, '', NULL,  now(),  now(), NULL);
INSERT INTO `sys_menu` VALUES (108, 36, '0,36', 'CRUD', 1, 'Curd', 'curd', 'demo/curd/index', NULL, NULL, 1, 1, 0, '', '', NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (109, 36, '0,36', '목록 선택기', 1, 'TableSelect', 'table-select', 'demo/table-select/index', NULL, NULL, 1, 1, 1, '', '', NULL, NULL, NULL);
INSERT INTO `sys_menu` VALUES (110, 0, '0', '라우트 파라미터', 2, NULL, '/route-param', 'Layout', NULL, 1, 1, 1, 11, 'el-icon-ElementPlus', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (111, 110, '0,110', '파라미터(type=1)', 1, 'RouteParamType1', 'route-param-type1', 'demo/route-param', NULL, 0, 1, 1, 1, 'el-icon-Star', NULL, now(), now(), '{\"type\": \"1\"}');
INSERT INTO `sys_menu` VALUES (112, 110, '0,110', '파라미터(type=2)', 1, 'RouteParamType2', 'route-param-type2', 'demo/route-param', NULL, 0, 1, 1, 2, 'el-icon-StarFilled', NULL, now(), now(), '{\"type\": \"2\"}');
INSERT INTO `sys_menu` VALUES (117, 1, '0,1', '시스템 로그', 1, 'Log', 'log', 'system/log/index', NULL, 0, 1, 1, 6, 'document', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (118, 0, '0', '시스템 도구', 2, NULL, '/codegen', 'Layout', NULL, 0, 1, 1, 2, 'menu', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (119, 118, '0,118', '코드 생성', 1, 'Codegen', 'codegen', 'codegen/index', NULL, 0, 1, 1, 1, 'code', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (120, 1, '0,1', '시스템 설정', 1, 'Config', 'config', 'system/config/index', NULL, 0, 1, 1, 7, 'setting', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (121, 120, '0,1,120', '시스템 설정 조회', 4, NULL, '', NULL, 'sys:config:query', 0, 1, 1, 1, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (122, 120, '0,1,120', '시스템 설정 추가', 4, NULL, '', NULL, 'sys:config:add', 0, 1, 1, 2, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (123, 120, '0,1,120', '시스템 설정 수정', 4, NULL, '', NULL, 'sys:config:update', 0, 1, 1, 3, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (124, 120, '0,1,120', '시스템 설정 삭제', 4, NULL, '', NULL, 'sys:config:delete', 0, 1, 1, 4, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (125, 120, '0,1,120', '시스템 설정 새로고침', 4, NULL, '', NULL, 'sys:config:refresh', 0, 1, 1, 5, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (126, 1, '0,1', '공지사항', 1, 'Notice', 'notice', 'system/notice/index', NULL, NULL, NULL, 1, 9, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (127, 126, '0,1,126', '공지 조회', 4, NULL, '', NULL, 'sys:notice:query', NULL, NULL, 1, 1, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (128, 126, '0,1,126', '공지 추가', 4, NULL, '', NULL, 'sys:notice:add', NULL, NULL, 1, 2, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (129, 126, '0,1,126', '공지 편집', 4, NULL, '', NULL, 'sys:notice:edit', NULL, NULL, 1, 3, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (130, 126, '0,1,126', '공지 삭제', 4, NULL, '', NULL, 'sys:notice:delete', NULL, NULL, 1, 4, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (133, 126, '0,1,126', '공지 발행', 4, NULL, '', NULL, 'sys:notice:publish', 0, 1, 1, 5, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (134, 126, '0,1,126', '공지 철회', 4, NULL, '', NULL, 'sys:notice:revoke', 0, 1, 1, 6, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (135, 1, '0,1', '사전 항목', 1, 'DictItem', 'dict-item', 'system/dict/dict-item', NULL, 0, 1, 0, 6, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (136, 135, '0,1,135', '사전 항목 추가', 4, NULL, '', NULL, 'sys:dict-item:add', NULL, NULL, 1, 2, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (137, 135, '0,1,135', '사전 항목 편집', 4, NULL, '', NULL, 'sys:dict-item:edit', NULL, NULL, 1, 3, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (138, 135, '0,1,135', '사전 항목 삭제', 4, NULL, '', NULL, 'sys:dict-item:delete', NULL, NULL, 1, 4, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (139, 3, '0,1,3', '역할 조회', 4, NULL, '', NULL, 'sys:role:query', NULL, NULL, 1, 1, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (140, 4, '0,1,4', '메뉴 조회', 4, NULL, '', NULL, 'sys:menu:query', NULL, NULL, 1, 1, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (141, 5, '0,1,5', '부서 조회', 4, NULL, '', NULL, 'sys:dept:query', NULL, NULL, 1, 1, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (142, 6, '0,1,6', '사전 조회', 4, NULL, '', NULL, 'sys:dict:query', NULL, NULL, 1, 1, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (143, 135, '0,1,135', '사전 항목 조회', 4, NULL, '', NULL, 'sys:dict-item:query', NULL, NULL, 1, 1, '', NULL, now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (144, 26, '0,26', '백엔드 문서', 3, NULL, 'https://youlai.blog.csdn.net/article/details/145178880', '', NULL, NULL, NULL, 1, 3, 'document', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (145, 26, '0,26', '모바일 문서', 3, NULL, 'https://youlai.blog.csdn.net/article/details/143222890', '', NULL, NULL, NULL, 1, 4, 'document', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (146, 36, '0,36', '드래그 컴포넌트', 1, 'Drag', 'drag', 'demo/drag', NULL, NULL, NULL, 1, 5, '', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (147, 36, '0,36', '스크롤 텍스트', 1, 'TextScroll', 'text-scroll', 'demo/text-scroll', NULL, NULL, NULL, 1, 6, '', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (148, 89, '0,89', '사전 실시간 동기화', 1, 'DictSync', 'dict-sync', 'demo/dict-sync', NULL, NULL, NULL, 1, 3, '', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (149, 89, '0,89', 'VxeTable', 1, 'VxeTable', 'vxe-table', 'demo/vxe-table/index', NULL, NULL, 1, 1, 0, 'el-icon-MagicStick', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (150, 36, '0,36', '자동 테이블 작업 열', 1, 'AutoOperationColumn', 'operation-column', 'demo/auto-operation-column', NULL, NULL, 1, 1, 1, '', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (151, 89, '0,89', 'CRUD 단일 파일', 1, 'CurdSingle', 'curd-single', 'demo/curd-single', NULL, NULL, 1, 1, 7, 'el-icon-Reading', '', now(),now(), NULL);
INSERT INTO `sys_menu` VALUES (152, 0, '0', 'AI 도우미', 2, NULL, '/platform', 'Layout', NULL, NULL, NULL, 1, 13, 'platform', '', now(), now(), NULL);
INSERT INTO `sys_menu` VALUES (153, 152, '0,152', 'AI 명령 기록', 1, 'AiCommandRecord', 'command-record', 'ai/command-record/index', NULL, NULL, 1, 1, 1, 'document', NULL, now(), now(), NULL);


-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `name` varchar(64) NOT NULL COMMENT '역할 이름',
                             `code` varchar(32) NOT NULL COMMENT '역할 코드',
                             `sort` int NULL COMMENT '표시 순서',
                             `status` tinyint(1) DEFAULT 1 COMMENT '역할 상태(1-정상 0-중지)',
                             `data_scope` tinyint NULL COMMENT '데이터 권한(1-모든데이터 2-부서및하위부서데이터 3-본부서데이터 4-본인데이터)',
                             `create_by` bigint NULL COMMENT '생성자 ID',
                             `create_time` datetime NULL COMMENT '생성 시간',
                             `update_by` bigint NULL COMMENT '수정자 ID',
                             `update_time` datetime NULL COMMENT '수정 시간',
                             `is_deleted` tinyint(1) DEFAULT 0 COMMENT '논리 삭제 플래그(0-삭제안됨 1-삭제됨)',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE COMMENT '역할 이름 고유 인덱스',
                             UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE COMMENT '역할 코드 고유 인덱스'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '역할 테이블';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '슈퍼 관리자', 'ROOT', 1, 1, 1, NULL, now(), NULL, now(), 0);
INSERT INTO `sys_role` VALUES (2, '시스템 관리자', 'ADMIN', 2, 1, 1, NULL, now(), NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (3, '방문 게스트', 'GUEST', 3, 1, 3, NULL, now(), NULL, now(), 0);
INSERT INTO `sys_role` VALUES (4, '시스템 관리자1', 'ADMIN1', 4, 1, 1, NULL, now(), NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (5, '시스템 관리자2', 'ADMIN2', 5, 1, 1, NULL, now(), NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (6, '시스템 관리자3', 'ADMIN3', 6, 1, 1, NULL, now(), NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (7, '시스템 관리자4', 'ADMIN4', 7, 1, 1, NULL, now(), NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (8, '시스템 관리자5', 'ADMIN5', 8, 1, 1, NULL, now(), NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (9, '시스템 관리자6', 'ADMIN6', 9, 1, 1, NULL, now(), NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (10, '시스템 관리자7', 'ADMIN7', 10, 1, 1, NULL, now(), NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (11, '시스템 관리자8', 'ADMIN8', 11, 1, 1, NULL, now(), NULL, NULL, 0);
INSERT INTO `sys_role` VALUES (12, '시스템 관리자9', 'ADMIN9', 12, 1, 1, NULL, now(), NULL, NULL, 0);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
                                  `role_id` bigint NOT NULL COMMENT '역할 ID',
                                  `menu_id` bigint NOT NULL COMMENT '메뉴 ID',
                                  UNIQUE INDEX `uk_roleid_menuid`(`role_id` ASC, `menu_id` ASC) USING BTREE COMMENT '역할 메뉴 고유 인덱스'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '역할 메뉴 연관 테이블';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 2);
INSERT INTO `sys_role_menu` VALUES (2, 3);
INSERT INTO `sys_role_menu` VALUES (2, 4);
INSERT INTO `sys_role_menu` VALUES (2, 5);
INSERT INTO `sys_role_menu` VALUES (2, 6);
INSERT INTO `sys_role_menu` VALUES (2, 20);
INSERT INTO `sys_role_menu` VALUES (2, 21);
INSERT INTO `sys_role_menu` VALUES (2, 22);
INSERT INTO `sys_role_menu` VALUES (2, 23);
INSERT INTO `sys_role_menu` VALUES (2, 24);
INSERT INTO `sys_role_menu` VALUES (2, 26);
INSERT INTO `sys_role_menu` VALUES (2, 30);
INSERT INTO `sys_role_menu` VALUES (2, 31);
INSERT INTO `sys_role_menu` VALUES (2, 32);
INSERT INTO `sys_role_menu` VALUES (2, 33);
INSERT INTO `sys_role_menu` VALUES (2, 36);
INSERT INTO `sys_role_menu` VALUES (2, 37);
INSERT INTO `sys_role_menu` VALUES (2, 38);
INSERT INTO `sys_role_menu` VALUES (2, 39);
INSERT INTO `sys_role_menu` VALUES (2, 40);
INSERT INTO `sys_role_menu` VALUES (2, 41);
INSERT INTO `sys_role_menu` VALUES (2, 70);
INSERT INTO `sys_role_menu` VALUES (2, 71);
INSERT INTO `sys_role_menu` VALUES (2, 72);
INSERT INTO `sys_role_menu` VALUES (2, 73);
INSERT INTO `sys_role_menu` VALUES (2, 74);
INSERT INTO `sys_role_menu` VALUES (2, 75);
INSERT INTO `sys_role_menu` VALUES (2, 76);
INSERT INTO `sys_role_menu` VALUES (2, 77);
INSERT INTO `sys_role_menu` VALUES (2, 78);
INSERT INTO `sys_role_menu` VALUES (2, 79);
INSERT INTO `sys_role_menu` VALUES (2, 81);
INSERT INTO `sys_role_menu` VALUES (2, 84);
INSERT INTO `sys_role_menu` VALUES (2, 85);
INSERT INTO `sys_role_menu` VALUES (2, 86);
INSERT INTO `sys_role_menu` VALUES (2, 87);
INSERT INTO `sys_role_menu` VALUES (2, 88);
INSERT INTO `sys_role_menu` VALUES (2, 89);
INSERT INTO `sys_role_menu` VALUES (2, 90);
INSERT INTO `sys_role_menu` VALUES (2, 91);
INSERT INTO `sys_role_menu` VALUES (2, 95);
INSERT INTO `sys_role_menu` VALUES (2, 97);
INSERT INTO `sys_role_menu` VALUES (2, 102);
INSERT INTO `sys_role_menu` VALUES (2, 105);
INSERT INTO `sys_role_menu` VALUES (2, 106);
INSERT INTO `sys_role_menu` VALUES (2, 107);
INSERT INTO `sys_role_menu` VALUES (2, 108);
INSERT INTO `sys_role_menu` VALUES (2, 109);
INSERT INTO `sys_role_menu` VALUES (2, 110);
INSERT INTO `sys_role_menu` VALUES (2, 111);
INSERT INTO `sys_role_menu` VALUES (2, 112);
INSERT INTO `sys_role_menu` VALUES (2, 114);
INSERT INTO `sys_role_menu` VALUES (2, 115);
INSERT INTO `sys_role_menu` VALUES (2, 116);
INSERT INTO `sys_role_menu` VALUES (2, 117);
INSERT INTO `sys_role_menu` VALUES (2, 118);
INSERT INTO `sys_role_menu` VALUES (2, 119);
INSERT INTO `sys_role_menu` VALUES (2, 120);
INSERT INTO `sys_role_menu` VALUES (2, 121);
INSERT INTO `sys_role_menu` VALUES (2, 122);
INSERT INTO `sys_role_menu` VALUES (2, 123);
INSERT INTO `sys_role_menu` VALUES (2, 124);
INSERT INTO `sys_role_menu` VALUES (2, 125);
INSERT INTO `sys_role_menu` VALUES (2, 126);
INSERT INTO `sys_role_menu` VALUES (2, 127);
INSERT INTO `sys_role_menu` VALUES (2, 128);
INSERT INTO `sys_role_menu` VALUES (2, 129);
INSERT INTO `sys_role_menu` VALUES (2, 130);
INSERT INTO `sys_role_menu` VALUES (2, 131);
INSERT INTO `sys_role_menu` VALUES (2, 132);
INSERT INTO `sys_role_menu` VALUES (2, 133);
INSERT INTO `sys_role_menu` VALUES (2, 134);
INSERT INTO `sys_role_menu` VALUES (2, 135);
INSERT INTO `sys_role_menu` VALUES (2, 136);
INSERT INTO `sys_role_menu` VALUES (2, 137);
INSERT INTO `sys_role_menu` VALUES (2, 138);
INSERT INTO `sys_role_menu` VALUES (2, 139);
INSERT INTO `sys_role_menu` VALUES (2, 140);
INSERT INTO `sys_role_menu` VALUES (2, 141);
INSERT INTO `sys_role_menu` VALUES (2, 142);
INSERT INTO `sys_role_menu` VALUES (2, 143);
INSERT INTO `sys_role_menu` VALUES (2, 144);
INSERT INTO `sys_role_menu` VALUES (2, 145);
INSERT INTO `sys_role_menu` VALUES (2, 146);
INSERT INTO `sys_role_menu` VALUES (2, 147);
INSERT INTO `sys_role_menu` VALUES (2, 148);
INSERT INTO `sys_role_menu` VALUES (2, 149);
INSERT INTO `sys_role_menu` VALUES (2, 150);
INSERT INTO `sys_role_menu` VALUES (2, 151);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `username` varchar(64) COMMENT '사용자명',
                             `nickname` varchar(64) COMMENT '닉네임',
                             `gender` tinyint(1) DEFAULT 1 COMMENT '성별((1-남성 2-여성 0-비공개)',
                             `password` varchar(100) COMMENT '비밀번호',
                             `dept_id` int COMMENT '부서 ID',
                             `avatar` varchar(255) COMMENT '사용자 아바타',
                             `mobile` varchar(20) COMMENT '연락처',
                             `status` tinyint(1) DEFAULT 1 COMMENT '상태(1-정상 0-비활성)',
                             `email` varchar(128) COMMENT '사용자 이메일',
                             `create_time` datetime COMMENT '생성 시간',
                             `create_by` bigint COMMENT '생성자 ID',
                             `update_time` datetime COMMENT '수정 시간',
                             `update_by` bigint COMMENT '수정자 ID',
                             `is_deleted` tinyint(1) DEFAULT 0 COMMENT '논리 삭제 플래그(0-삭제안됨 1-삭제됨)',
                             `openid` char(28) COMMENT '위챗 openid',
                             PRIMARY KEY (`id`) USING BTREE,
                             KEY `login_name` (`username`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '사용자 정보 테이블';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'root', '유라이 기술', 0, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', NULL, 'https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif', '18812345677', 1, 'youlaitech@163.com', now(), NULL, now(), NULL, 0,NULL);
INSERT INTO `sys_user` VALUES (2, 'admin', '시스템 관리자', 1, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', 1, 'https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif', '18812345678', 1, 'youlaitech@163.com', now(), NULL, now(), NULL, 0,NULL);
INSERT INTO `sys_user` VALUES (3, 'test', '테스트 사용자', 1, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', 3, 'https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif', '18812345679', 1, 'youlaitech@163.com', now(), NULL, now(), NULL, 0,NULL);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
                                  `user_id` bigint NOT NULL COMMENT '사용자 ID',
                                  `role_id` bigint NOT NULL COMMENT '역할 ID',
                                  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '사용자 역할 연관 테이블';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (3, 3);


-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '기본키',
                           `module` varchar(50) NOT NULL COMMENT '로그 모듈',
                           `request_method` varchar(64) NOT NULL COMMENT '요청 방식',
                           `request_params` text COMMENT '요청 파라미터(대량 요청 파라미터가 text를 초과할 수 있음)',
                           `response_content` mediumtext COMMENT '반환 파라미터',
                           `content` varchar(255) NOT NULL COMMENT '로그 내용',
                           `request_uri` varchar(255) COMMENT '요청 경로',
                           `method` varchar(255) COMMENT '메서드명',
                           `ip` varchar(45) COMMENT 'IP 주소',
                           `province` varchar(100) COMMENT '도',
                           `city` varchar(100) COMMENT '시',
                           `execution_time` bigint COMMENT '실행 시간(ms)',
                           `browser` varchar(100) COMMENT '브라우저',
                           `browser_version` varchar(100) COMMENT '브라우저 버전',
                           `os` varchar(100) COMMENT '운영체제',
                           `create_by` bigint COMMENT '생성자 ID',
                           `create_time` datetime COMMENT '생성 시간',
                           `is_deleted` tinyint DEFAULT '0' COMMENT '논리 삭제 플래그(1-삭제됨 0-삭제안됨)',
                           PRIMARY KEY (`id`) USING BTREE,
                           KEY `idx_create_time` (`create_time`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COMMENT='시스템 로그 테이블';

-- ----------------------------
-- Table structure for gen_config
-- ----------------------------
DROP TABLE IF EXISTS `gen_config`;
CREATE TABLE `gen_config` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `table_name` varchar(100) NOT NULL COMMENT '테이블명',
                              `module_name` varchar(100) COMMENT '모듈명',
                              `package_name` varchar(255) NOT NULL COMMENT '패키지명',
                              `business_name` varchar(100) NOT NULL COMMENT '비즈니스명',
                              `entity_name` varchar(100) NOT NULL COMMENT '엔티티 클래스명',
                              `author` varchar(50) NOT NULL COMMENT '작성자',
                              `parent_menu_id` bigint COMMENT '상위 메뉴 ID, sys_menu의 id에 대응',
                              `remove_table_prefix` varchar(20) COMMENT '제거할 테이블 접두사, 예: sys_',
                              `page_type` varchar(20) COMMENT '페이지 유형(classic|curd)',
                              `create_time` datetime COMMENT '생성 시간',
                              `update_time` datetime COMMENT '수정 시간',
                              `is_deleted` tinyint(4) DEFAULT 0 COMMENT '삭제 여부',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_tablename` (`table_name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='코드 생성 기본 설정 테이블';

-- ----------------------------
-- Table structure for gen_field_config
-- ----------------------------
DROP TABLE IF EXISTS `gen_field_config`;
CREATE TABLE `gen_field_config` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `config_id` bigint NOT NULL COMMENT '연관 설정 ID',
                                    `column_name` varchar(100)  ,
                                    `column_type` varchar(50)  ,
                                    `column_length` int ,
                                    `field_name` varchar(100) NOT NULL COMMENT '필드명',
                                    `field_type` varchar(100) COMMENT '필드 유형',
                                    `field_sort` int COMMENT '필드 정렬',
                                    `field_comment` varchar(255) COMMENT '필드 설명',
                                    `max_length` int ,
                                    `is_required` tinyint(1) COMMENT '필수 여부',
                                    `is_show_in_list` tinyint(1) DEFAULT '0' COMMENT '목록에 표시 여부',
                                    `is_show_in_form` tinyint(1) DEFAULT '0' COMMENT '폼에 표시 여부',
                                    `is_show_in_query` tinyint(1) DEFAULT '0' COMMENT '조회 조건에 표시 여부',
                                    `query_type` tinyint COMMENT '조회 방식',
                                    `form_type` tinyint COMMENT '폼 유형',
                                    `dict_type` varchar(50) COMMENT '사전 유형',
                                    `create_time` datetime COMMENT '생성 시간',
                                    `update_time` datetime COMMENT '수정 시간',
                                    PRIMARY KEY (`id`),
                                    KEY `config_id` (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='코드 생성 필드 설정 테이블';

-- ----------------------------
-- 시스템 설정 테이블
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `config_name` varchar(50) NOT NULL COMMENT '설정 이름',
                              `config_key` varchar(50) NOT NULL COMMENT '설정 key',
                              `config_value` varchar(100) NOT NULL COMMENT '설정 값',
                              `remark` varchar(255) COMMENT '비고',
                              `create_time` datetime COMMENT '생성 시간',
                              `create_by` bigint COMMENT '생성자 ID',
                              `update_time` datetime COMMENT '수정 시간',
                              `update_by` bigint COMMENT '수정자 ID',
                              `is_deleted` tinyint(4) DEFAULT '0' NOT NULL COMMENT '논리 삭제 플래그(0-삭제안됨 1-삭제됨)',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='시스템 설정 테이블';

INSERT INTO `sys_config` VALUES (1, '시스템 제한 QPS', 'IP_QPS_THRESHOLD_LIMIT', '10', '단일 IP 요청의 최대 초당 쿼리 수(QPS) 임계값 Key', now(), 1, NULL, NULL, 0);

-- ----------------------------
-- 공지사항 테이블
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `title` varchar(50) COMMENT '공지 제목',
                              `content` text COMMENT '공지 내용',
                              `type` tinyint NOT NULL COMMENT '공지 유형（연관 사전 코드：notice_type）',
                              `level` varchar(5) NOT NULL COMMENT '공지 수준（사전 code：notice_level）',
                              `target_type` tinyint NOT NULL COMMENT '대상 유형（1: 전체, 2: 지정）',
                              `target_user_ids` varchar(255) COMMENT '대상자 ID 집합（여러 개는 쉼표로 구분）',
                              `publisher_id` bigint COMMENT '발행자 ID',
                              `publish_status` tinyint DEFAULT '0' COMMENT '발행 상태（0: 미발행, 1: 발행됨, -1: 철회됨）',
                              `publish_time` datetime COMMENT '발행 시간',
                              `revoke_time` datetime COMMENT '철회 시간',
                              `create_by` bigint NOT NULL COMMENT '생성자 ID',
                              `create_time` datetime NOT NULL COMMENT '생성 시간',
                              `update_by` bigint COMMENT '수정자 ID',
                              `update_time` datetime COMMENT '수정 시간',
                              `is_deleted` tinyint(1) DEFAULT '0' COMMENT '삭제 여부（0: 삭제안됨, 1: 삭제됨）',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='공지사항 테이블';

INSERT INTO `sys_notice`  VALUES (1, 'v2.12.0 시스템 로그, 접근 추세 통계 기능 추가.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 1, 'L', 1, '2', 1, 1, now(), now(), 2, now(), 1, now(), 0);
INSERT INTO `sys_notice`  VALUES (2, 'v2.13.0 메뉴 검색 추가.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 1, 'L', 1, '2', 1, 1, now(), now(), 2, now(), 1, now(), 0);
INSERT INTO `sys_notice`  VALUES (3, 'v2.14.0 개인 센터 추가.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 1, 'L', 1, '2', 2, 1, now(), now(), 2, now(), 2, now(), 0);
INSERT INTO `sys_notice`  VALUES (4, 'v2.15.0 로그인 페이지 개편.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 1, 'L', 1, '2', 2, 1, now(), now(), 2, now(), 2, now(), 0);
INSERT INTO `sys_notice`  VALUES (5, 'v2.16.0 공지사항, 사전 번역 컴포넌트.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 1, 'L', 1, '2', 2, 1, now(), now(), 2, now(), 2, now(), 0);
INSERT INTO `sys_notice`  VALUES (6, '시스템은 이번 주 토요일 새벽 2시에 유지보수를 진행하며, 예상 유지보수 시간은 2시간입니다.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 2, 'H', 1, '2', 2, 1, now(), now(), 2, now(), 2, now(), 0);
INSERT INTO `sys_notice`  VALUES (7, '최근 피싱 이메일이 발견되었으니, 낯선 링크를 클릭하지 마시기 바랍니다.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 3, 'L', 1, '2', 2, 1, now(), now(), 2, now(), 2, now(), 0);
INSERT INTO `sys_notice`  VALUES (8, '국경절 연휴는 10월 1일부터 10월 7일까지 7일간입니다.', '<p>1. 메시지 알림</p><p>2. 사전 재구성</p><p>3. 코드 생성</p>', 4, 'L', 1, '2', 2, 1, now(), now(), 2, now(), 2, now(), 0);
INSERT INTO `sys_notice`  VALUES (9, '회사는 10월 15일에 신제품 발표회를 개최합니다. 많은 기대 부탁드립니다.', '회사는 10월 15일에 신제품 발표회를 개최합니다. 많은 기대 부탁드립니다.', 5, 'H', 1, '2', 2, 1, now(), now(), 2, now(), 2, now(), 0);
INSERT INTO `sys_notice`  VALUES (10, 'v2.16.1 버전 출시.', 'v2.16.1 버전은 WebSocket 중복 연결로 인한 백엔드 스레드 차단 문제를 수정하고, 공지사항을 최적화했습니다.', 1, 'M', 1, '2', 2, 1, now(), now(), 2, now(), 2, now(), 0);

-- ----------------------------
-- 사용자 공지사항 테이블
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_notice`;
CREATE TABLE `sys_user_notice` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                                   `notice_id` bigint NOT NULL COMMENT '공지 id',
                                   `user_id` bigint NOT NULL COMMENT '사용자 id',
                                   `is_read` bigint DEFAULT '0' COMMENT '읽음 상태（0: 읽지않음, 1: 읽음）',
                                   `read_time` datetime COMMENT '읽은 시간',
                                   `create_time` datetime NOT NULL COMMENT '생성 시간',
                                   `update_time` datetime COMMENT '수정 시간',
                                   `is_deleted` tinyint DEFAULT '0' COMMENT '논리 삭제(0: 삭제안됨, 1: 삭제됨)',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='사용자 공지사항 테이블';

INSERT INTO `sys_user_notice` VALUES (1, 1, 2, 1, NULL, now(), now(), 0);
INSERT INTO `sys_user_notice` VALUES (2, 2, 2, 1, NULL, now(), now(), 0);
INSERT INTO `sys_user_notice` VALUES (3, 3, 2, 1, NULL, now(), now(), 0);
INSERT INTO `sys_user_notice` VALUES (4, 4, 2, 1, NULL, now(), now(), 0);
INSERT INTO `sys_user_notice` VALUES (5, 5, 2, 1, NULL, now(), now(), 0);
INSERT INTO `sys_user_notice` VALUES (6, 6, 2, 1, NULL, now(), now(), 0);
INSERT INTO `sys_user_notice` VALUES (7, 7, 2, 1, NULL, now(), now(), 0);
INSERT INTO `sys_user_notice` VALUES (8, 8, 2, 1, NULL, now(), now(), 0);
INSERT INTO `sys_user_notice` VALUES (9, 9, 2, 1, NULL, now(), now(), 0);
INSERT INTO `sys_user_notice` VALUES (10, 10, 2, 1, NULL, now(), now(), 0);

-- ----------------------------
-- AI 명령 기록 테이블
-- ----------------------------
DROP TABLE IF EXISTS `ai_command_record`;
CREATE TABLE `ai_command_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '기본키 ID',
  `user_id` bigint DEFAULT NULL COMMENT '사용자 ID',
  `username` varchar(64) DEFAULT NULL COMMENT '사용자명',
  `original_command` text COMMENT '원본 명령',
  -- 파싱 관련 필드
  `provider` varchar(32) DEFAULT NULL COMMENT 'AI 공급자(qwen/openai/deepseek/gemini 등)',
  `model` varchar(64) DEFAULT NULL COMMENT 'AI 모델(qwen-plus/qwen-max/gpt-4-turbo 등)',
  `parse_success` tinyint(1) DEFAULT NULL COMMENT '파싱 성공 여부(0-실패, 1-성공)',
  `function_calls` text COMMENT '파싱된 함수 호출 목록(JSON)',
  `explanation` varchar(500) DEFAULT NULL COMMENT 'AI의 이해 설명',
  `confidence` decimal(3,2) DEFAULT NULL COMMENT '신뢰도(0.00-1.00)',
  `parse_error_message` text COMMENT '파싱 오류 메시지',
  `input_tokens` int DEFAULT NULL COMMENT '입력 토큰 수',
  `output_tokens` int DEFAULT NULL COMMENT '출력 토큰 수',
  `total_tokens` int DEFAULT NULL COMMENT '총 토큰 수',
  `parse_time` bigint DEFAULT NULL COMMENT '파싱 소요 시간(밀리초)',
  -- 실행 관련 필드
  `function_name` varchar(255) DEFAULT NULL COMMENT '실행된 함수명',
  `function_arguments` text COMMENT '함수 인자(JSON)',
  `execute_status` varchar(20) DEFAULT NULL COMMENT '실행 상태(pending-대기중, success-성공, failed-실패)',
  `execute_result` text COMMENT '실행 결과(JSON)',
  `execute_error_message` text COMMENT '실행 오류 메시지',
  `affected_rows` int DEFAULT NULL COMMENT '영향받은 레코드 수',
  `is_dangerous` tinyint(1) DEFAULT '0' COMMENT '위험 작업 여부(0-아니오, 1-예)',
  `requires_confirmation` tinyint(1) DEFAULT '0' COMMENT '확인 필요 여부(0-아니오, 1-예)',
  `user_confirmed` tinyint(1) DEFAULT NULL COMMENT '사용자 확인 여부(0-아니오, 1-예)',
  `idempotency_key` varchar(128) DEFAULT NULL COMMENT '멱등성 토큰(중복 실행 방지)',
  `execution_time` bigint DEFAULT NULL COMMENT '실행 소요 시간(밀리초)',
  -- 공통 필드
  `ip_address` varchar(128) DEFAULT NULL COMMENT 'IP 주소',
  `user_agent` varchar(512) DEFAULT NULL COMMENT '사용자 에이전트',
  `current_route` varchar(255) DEFAULT NULL COMMENT '현재 페이지 라우트',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
  `remark` varchar(500) DEFAULT NULL COMMENT '비고',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_idempotency_key` (`idempotency_key`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_provider` (`provider`),
  KEY `idx_model` (`model`),
  KEY `idx_parse_success` (`parse_success`),
  KEY `idx_execute_status` (`execute_status`),
  KEY `idx_is_dangerous` (`is_dangerous`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='AI 명령 기록 테이블';


SET FOREIGN_KEY_CHECKS = 1;
