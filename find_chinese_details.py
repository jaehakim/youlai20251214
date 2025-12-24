#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Find detailed information about remaining Chinese characters.
Shows file, line number, and context.
"""

import re
from pathlib import Path
from collections import defaultdict

CHINESE_PATTERN = re.compile(r'[\u4E00-\u9FFF]+')
EXCLUDE_DIRS = {'.git', 'node_modules', '.vscode', '.idea', 'target', 'dist', 'build', '__pycache__'}
EXCLUDE_FILES = {'zh-cn.json', 'zh-hans.json', 'zh-hant.json'}  # Language files, should keep Chinese

files_with_locations = defaultdict(list)

# Focus on mock, JSON, Markdown, and remaining source files
patterns = ['**/*.mock.ts', '**/*.json', '**/*.md', '**/notice.ts']

base_path = Path(".")

for pattern in patterns:
    for file_path in base_path.glob(pattern):
        # Skip excluded directories and files
        if any(exc in file_path.parts for exc in EXCLUDE_DIRS):
            continue
        if file_path.name in EXCLUDE_FILES:
            continue

        try:
            lines = file_path.read_text(encoding='utf-8', errors='ignore').split('\n')
        except:
            continue

        for line_num, line in enumerate(lines, 1):
            matches = CHINESE_PATTERN.findall(line)
            if matches:
                files_with_locations[str(file_path)].append({
                    'line': line_num,
                    'content': line.strip()[:100],
                    'count': len(''.join(matches))
                })

print("\n" + "="*70)
print("중국어 문자가 발견된 파일 상세 정보")
print("="*70 + "\n")

for file_path in sorted(files_with_locations.keys()):
    locations = files_with_locations[file_path]
    print(f"\nFile: {file_path}")
    print(f"Lines with Chinese: {len(locations)}")
    for loc in locations[:3]:  # Show first 3 occurrences
        print(f"  - Line {loc['line']}: {loc['count']} characters")
    if len(locations) > 3:
        print(f"  ... and {len(locations) - 3} more lines")

print("\n" + "="*70)
