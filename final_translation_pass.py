#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import os

# Final comprehensive translation pass
FINAL_TRANS = {
    # Complete remaining phrases and words
    '结果': '결과',
    '登出': '로그아웃',
    '参数': '파라미터',
    '创建': '생성',
    '用于': '용도',
    '비밀번호인증의토큰': '비밀번호 인증 토큰',
    '未인증': '인증 안 됨',
    '执行인증': '인증 실행',
    '인증中': '인증 중',
    '성공后': '성공 후',
    '并存入': '및 저장',
    '供로그인로그': '로그인 로그용',
    '使用': '사용',
    '已인증': '인증 완료',
    '一键인증로그인': '원클릭 인증 로그인',
    '사용자위챗인증의토큰': '사용자 위챗 인증 토큰',
    '随机生성': '랜덤 생성',
    '位': '자리',
    '固정为': '고정값',
    '为了방편측试': '테스트 편의를 위해',
    '실제개발中': '실제 개발 시',
    '설정了厂商SMS서비스后': 'SMS 서비스 설정 후',
    '可는使용上면의随机인증코드': '위의 랜덤 인증코드 사용 가능',
    '캐시인증코드至': '인증코드를 캐시에 저장',
    '用于로그인校验': '로그인 검증용',
    '사용자SMS인증코드인증의토큰': '사용자 SMS 인증코드 인증 토큰',
    '将JWT토큰가入黑명单': 'JWT 토큰을 블랙리스트에 추가',
    '清제Security상하文': 'Security 컨텍스트 초기화',
    '인증코드文本캐시至': '인증코드 텍스트를 캐시에 저장',
    '新의접근토큰': '새로운 접근 토큰',
    '创建위챗미니 프로그램인증토큰': '위챗 미니 프로그램 인증 토큰 생성',
    '创建위챗미니 프로그램휴대폰 번호인증': '위챗 미니 프로그램 휴대폰 번호 인증 생성',
    '执행인증': '인증 실행',
    '성공后生성': '성공 후 생성',
    '并存入': '및 저장',

    '创建사용자': '사용자 생성',
    '创建': '생성',
    '一键': '원클릭',
    '성공后': '성공 후',
    '生成': '생성',
    '并存': '및 저장',
    '存入': '저장',
    '上下文': '컨텍스트',
    '供': '용',
    '使用': '사용',
    '已': '이미',
    '随机': '랜덤',
    '位': '자리',
    '固정': '고정',
    '为': '값',
    '为了': '위해',
    '方便': '편의',
    '测试': '테스트',
    '实际': '실제',
    '开发': '개발',
    '中': '중',
    '在': '에',
    '了': '',
    '厂商': '벤더',
    '后': '후',
    '可以': '가능',
    '可': '가',
    '上면': '위',
    '将': '을',
    '加入': '추가',
    '黑명单': '블랙리스트',
    '清제': '초기화',
    '清除': '제거',
    '文本': '텍스트',
    '至': '에',
    '新': '새',
    '用于': '용도',
    '校验': '검증',
    '密码': '비밀번호',
    '的': '',
    '未': '미',
    '中': '중',
    '成功': '성공',
    '一': '원',
    '键': '클릭',
}

# Sort by length (longest first)
trans_sorted = sorted(FINAL_TRANS.items(), key=lambda x: len(x[0]), reverse=True)

BASE_PATH = r"C:\TMP\2025121401_youlai_4\youlai20251214\youlai-boot\src\main\java\com\youlai\boot"

count = 0
for root, dirs, files in os.walk(BASE_PATH):
    for file in files:
        if file.endswith('.java') and ('controller' in root or 'service' in root):
            fpath = os.path.join(root, file)
            try:
                with open(fpath, 'r', encoding='utf-8') as f:
                    content = f.read()
                orig = content
                for cn, kr in trans_sorted:
                    if cn:  # Skip empty strings
                        content = content.replace(cn, kr)
                if content != orig:
                    with open(fpath, 'w', encoding='utf-8') as f:
                        f.write(content)
                    count += 1
                    print(f'[OK] {os.path.relpath(fpath, BASE_PATH)}')
            except Exception as e:
                print(f'[ERROR] {fpath}: {e}')

print(f'\nTotal files updated: {count}')
