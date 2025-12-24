#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Extract all remaining Chinese characters/terms and create a translation mapping.
"""

import re
from pathlib import Path
from collections import Counter

CHINESE_PATTERN = re.compile(r'[\u4E00-\u9FFF]+')
EXCLUDE_DIRS = {'.git', 'node_modules', '.vscode', '.idea', 'target', 'dist', 'build', '__pycache__'}
EXCLUDE_FILES = {'zh-cn.json', 'zh-hans.json', 'zh-hant.json'}

all_chinese_terms = Counter()

base_path = Path(".")

# Find all files with Chinese
for file_path in base_path.glob("**/*"):
    if file_path.is_dir():
        continue
    if any(exc in file_path.parts for exc in EXCLUDE_DIRS):
        continue
    if file_path.name in EXCLUDE_FILES:
        continue

    # Only check relevant file types
    if file_path.suffix not in {'.ts', '.tsx', '.vue', '.md', '.json', '.yaml', '.yml'}:
        continue

    try:
        content = file_path.read_text(encoding='utf-8', errors='ignore')
    except:
        continue

    matches = CHINESE_PATTERN.findall(content)
    for match in matches:
        all_chinese_terms[match] += 1

# Sort by frequency
print("\n" + "="*70)
print("Remaining Chinese Terms (sorted by frequency)")
print("="*70 + "\n")

total_terms = len(all_chinese_terms)
total_occurrences = sum(all_chinese_terms.values())

print(f"Total unique terms: {total_terms}")
print(f"Total occurrences: {total_occurrences}\n")

print("Top 100 most frequent Chinese terms:")
print("-"*70)

for i, (term, count) in enumerate(all_chinese_terms.most_common(100), 1):
    # Write to file to avoid encoding issues
    try:
        term_display = f"[{len(term)} chars]"
        print(f"{i:3}. {term_display} - {count} occurrences")
    except:
        print(f"{i:3}. [unreadable] - {count} occurrences")

# Also save to file for reference
with open("remaining_chinese_terms.txt", "w", encoding='utf-8') as f:
    f.write("All Remaining Chinese Terms (sorted by frequency):\n")
    f.write("="*70 + "\n\n")
    for term, count in all_chinese_terms.most_common():
        f.write(f"{term} - {count} occurrences\n")

print("\n[INFO] Saved to remaining_chinese_terms.txt")
print("="*70)
