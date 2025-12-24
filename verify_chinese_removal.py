#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Verify that no remaining Chinese characters exist in the codebase.
Checks all relevant file types: Java, Vue, TypeScript, XML, YAML, Markdown, etc.
"""

import os
import re
from pathlib import Path
from collections import Counter, defaultdict

# Chinese character pattern (CJK Unified Ideographs)
CHINESE_PATTERN = re.compile(r'[\u4E00-\u9FFF]+')

# File types to check
FILE_TYPES = {
    '*.java': 'Java',
    '*.vue': 'Vue',
    '*.ts': 'TypeScript',
    '*.tsx': 'TypeScript JSX',
    '*.xml': 'XML',
    '*.yml': 'YAML',
    '*.yaml': 'YAML',
    '*.md': 'Markdown',
    '*.json': 'JSON',
}

def check_file_for_chinese(file_path: Path) -> tuple:
    """
    Check a file for Chinese characters.
    Returns (has_chinese, count, examples)
    """
    try:
        content = file_path.read_text(encoding='utf-8', errors='ignore')
    except Exception as e:
        return False, 0, []

    # Find all Chinese character sequences
    matches = CHINESE_PATTERN.findall(content)

    if not matches:
        return False, 0, []

    # Count total Chinese characters
    total_count = sum(len(m) for m in matches)

    # Get unique examples (up to 10)
    unique_examples = list(set(matches))[:10]

    return True, total_count, unique_examples

def main():
    base_path = Path(".")

    # Exclude directories
    exclude_dirs = {'.git', 'node_modules', '.vscode', '.idea', 'target', 'dist', 'build', '__pycache__'}

    results_by_type = defaultdict(lambda: {
        'files_with_chinese': [],
        'total_chars': 0,
        'examples': Counter()
    })

    print("\n" + "="*70)
    print("중국어 문자 검증 - 모든 파일 타입")
    print("="*70 + "\n")

    # Find all relevant files
    all_files = []
    for file_type, description in FILE_TYPES.items():
        files = list(base_path.glob(f"**/{file_type}"))
        # Filter out excluded directories
        files = [f for f in files if not any(exc in f.parts for exc in exclude_dirs)]
        for f in files:
            all_files.append((f, description, file_type))

    print(f"검사 중인 파일: {len(all_files)}개\n")

    # Check each file
    for file_path, file_type_desc, file_type in sorted(all_files):
        has_chinese, count, examples = check_file_for_chinese(file_path)

        if has_chinese:
            results_by_type[file_type_desc]['files_with_chinese'].append(file_path)
            results_by_type[file_type_desc]['total_chars'] += count
            for example in examples:
                results_by_type[file_type_desc]['examples'][example] += 1

    # Print results by file type
    total_files_with_chinese = 0
    total_chinese_chars = 0

    for file_type in sorted(FILE_TYPES.values()):
        if file_type not in results_by_type:
            print(f"[OK] {file_type}: 중국어 문자 없음")
        else:
            data = results_by_type[file_type]
            num_files = len(data['files_with_chinese'])
            num_chars = data['total_chars']
            total_files_with_chinese += num_files
            total_chinese_chars += num_chars

            print(f"\n[ERROR] {file_type}: {num_files}개 파일, {num_chars}개 중국어 문자 발견")
            print(f"   파일 목록:")
            for f in sorted(data['files_with_chinese'])[:5]:  # Show first 5
                print(f"     - {f}")
            if len(data['files_with_chinese']) > 5:
                print(f"     ... 외 {len(data['files_with_chinese']) - 5}개 파일")

            if data['examples']:
                print(f"   예시 단어 (상위 10개):")
                for word, freq in data['examples'].most_common(10):
                    # Skip printing the actual Chinese word due to encoding issues
                    print(f"     - [{len(word)}글자] ({freq}회)")

    # Summary
    print("\n" + "="*70)
    print("검증 결과 요약")
    print("="*70)

    if total_files_with_chinese == 0:
        print("\n[SUCCESS] 성공! 모든 파일에서 중국어 문자가 제거되었습니다.")
        print(f"   검사한 파일: {len(all_files)}개")
        print(f"   발견된 중국어: 0개")
        return True
    else:
        print(f"\n[WARNING] 경고! 다음 파일들에서 중국어 문자가 발견되었습니다:")
        print(f"   파일 수: {total_files_with_chinese}개")
        print(f"   총 중국어 문자: {total_chinese_chars}개")
        return False

if __name__ == "__main__":
    success = main()
    exit(0 if success else 1)
