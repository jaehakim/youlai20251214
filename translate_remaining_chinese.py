#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Translate remaining Chinese characters in mock data, JSON configs, and documentation.
Focuses on files that still contain Chinese after previous translation passes.
"""

import os
import re
from pathlib import Path
from typing import Dict, List, Tuple

# Extended translation dictionary (includes mock data and common terms)
TRANSLATION_DICT = {
    # User/System related
    "系统管理员": "시스템 관리자",
    "测试小用户": "테스트 사용자",
    "普通用户": "일반 사용자",
    "超级管理员": "슈퍼 관리자",
    "部门管理员": "부서 관리자",
    "财务": "재무",
    "销售": "판매",
    "技术": "기술",
    "人事": "인사",
    "市场": "마케팅",

    # Roles
    "管理员": "관리자",
    "用户": "사용자",
    "访客": "게스트",

    # Status
    "启用": "활성화",
    "禁用": "비활성화",
    "正常": "정상",
    "停用": "중지됨",
    "删除": "삭제됨",

    # Department related
    "财务部": "재무부",
    "销售部": "판매부",
    "技术部": "기술부",
    "人事部": "인사부",
    "市场部": "마케팅부",
    "总经理": "회장",
    "副总": "부회장",
    "经理": "매니저",
    "主管": "팀장",

    # Menu/Navigation
    "首页": "홈",
    "仪表板": "대시보드",
    "工作台": "워크벤치",
    "系统管理": "시스템 관리",
    "用户管理": "사용자 관리",
    "角色管理": "역할 관리",
    "权限管理": "권한 관리",
    "菜单管理": "메뉴 관리",
    "部门管理": "부서 관리",
    "字典管理": "사전 관리",
    "代码生成": "코드 생성",
    "项目文档": "프로젝트 문서",

    # Notice/Messages
    "系统公告": "시스템 공지",
    "维护通知": "유지보수 알림",
    "更新日志": "업데이트 로그",
    "重要通知": "중요 공지",
    "新闻资讯": "뉴스",

    # Common attributes
    "名称": "이름",
    "描述": "설명",
    "状态": "상태",
    "排序": "정렬 순서",
    "创建时间": "생성 시간",
    "更新时间": "업데이트 시간",
    "备注": "비고",
    "邮箱": "이메일",
    "电话": "전화",
    "代码": "코드",
    "值": "값",

    # Types
    "菜单": "메뉴",
    "按钮": "버튼",
    "详情": "세부사항",
    "列表": "목록",

    # Actions
    "查看": "보기",
    "编辑": "편집",
    "删除": "삭제",
    "新增": "신규",
    "导出": "내보내기",
    "导入": "가져오기",
    "下载": "다운로드",
    "上传": "업로드",

    # Documentation
    "文档": "문서",
    "说明": "설명",
    "注意": "주의",
    "示例": "예제",
    "使用": "사용",
    "配置": "설정",
    "安装": "설치",
    "运行": "실행",
    "构建": "빌드",
    "测试": "테스트",

    # Technical
    "项目": "프로젝트",
    "模块": "모듈",
    "组件": "컴포넌트",
    "页面": "페이지",
    "路由": "라우트",
    "存储": "저장소",
    "服务": "서비스",
    "API": "API",
    "端点": "엔드포인트",

    # Version/Release
    "版本": "버전",
    "发布": "릴리스",
    "更新": "업데이트",
    "修复": "수정",
    "特性": "기능",
    "改进": "개선",
    "新增": "신규",
    "优化": "최적화",
    "废弃": "구식",
}

def translate_file(file_path: Path, trans_dict: Dict[str, str]) -> Tuple[bool, int]:
    """
    Translate a single file from Chinese to Korean.
    Returns (was_translated, lines_changed)
    """
    try:
        content = file_path.read_text(encoding='utf-8')
    except Exception as e:
        print(f"[ERROR] Failed to read {file_path}: {e}")
        return False, 0

    original_content = content

    # Sort by length (longest first) to avoid partial replacements
    for chinese, korean in sorted(trans_dict.items(), key=lambda x: len(x[0]), reverse=True):
        content = content.replace(chinese, korean)

    # Check if anything changed
    if content == original_content:
        return False, 0

    # Count lines changed
    original_lines = original_content.split('\n')
    new_lines = content.split('\n')
    lines_changed = sum(1 for a, b in zip(original_lines, new_lines) if a != b)
    lines_changed += abs(len(original_lines) - len(new_lines))

    try:
        file_path.write_text(content, encoding='utf-8')
        return True, lines_changed
    except Exception as e:
        print(f"[ERROR] Failed to write {file_path}: {e}")
        return False, 0

def main():
    base_path = Path(".")

    # Files to translate
    mock_files = list(base_path.glob("vue3-element-admin/mock/**/*.ts"))
    json_files = [
        Path("vue3-element-admin/package.json"),
        Path("vue3-element-admin/tsconfig.json"),
        Path("youlai-boot/src/main/resources/META-INF/additional-spring-configuration-metadata.json"),
    ]
    md_files = [
        Path("vue3-element-admin/CHANGELOG.md"),
        Path("vue3-element-admin/README.md"),
        Path("vue3-element-admin/README.en-US.md"),
        Path("youlai-boot/README.md"),
        Path("youlai-boot/docker/run.md"),
    ]

    all_files = mock_files + json_files + md_files
    # Filter existing files
    all_files = [f for f in all_files if f.exists()]

    print("\n" + "="*60)
    print("나머지 중국어 문자 번역")
    print("="*60)

    total_translated = 0
    total_lines = 0

    print("\n[Mock 데이터 파일]")
    for mock_file in sorted(mock_files):
        was_translated, lines_changed = translate_file(mock_file, TRANSLATION_DICT)
        if was_translated:
            total_translated += 1
            total_lines += lines_changed
            print(f"[SUCCESS] {mock_file.name} ({lines_changed} 줄)")

    print("\n[JSON 설정 파일]")
    for json_file in json_files:
        if json_file.exists():
            was_translated, lines_changed = translate_file(json_file, TRANSLATION_DICT)
            if was_translated:
                total_translated += 1
                total_lines += lines_changed
                print(f"[SUCCESS] {json_file.name} ({lines_changed} 줄)")

    print("\n[Markdown 문서 파일]")
    for md_file in md_files:
        if md_file.exists():
            was_translated, lines_changed = translate_file(md_file, TRANSLATION_DICT)
            if was_translated:
                total_translated += 1
                total_lines += lines_changed
                print(f"[SUCCESS] {md_file.name} ({lines_changed} 줄)")

    print("\n" + "="*60)
    print(f"번역 요약: {total_translated}개 파일, {total_lines}줄 변경")
    print("="*60)

if __name__ == "__main__":
    main()
