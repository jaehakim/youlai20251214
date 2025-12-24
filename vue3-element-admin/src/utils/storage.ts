import { STORAGE_KEYS, APP_PREFIX } from "@/constants";

/**
 * 스토리지 유틸리티 클래스
 * localStorage 및 sessionStorage 작업 방법 제공
 */
export class Storage {
  /**
   * localStorage 저장
   */
  static set(key: string, value: any): void {
    localStorage.setItem(key, JSON.stringify(value));
  }

  static get<T>(key: string, defaultValue?: T): T {
    const value = localStorage.getItem(key);
    if (!value) return defaultValue as T;

    try {
      return JSON.parse(value);
    } catch {
      // 파싱 실패 시 원본 문자열 반환
      return value as unknown as T;
    }
  }

  static remove(key: string): void {
    localStorage.removeItem(key);
  }

  /**
   * sessionStorage 저장
   */
  static sessionSet(key: string, value: any): void {
    sessionStorage.setItem(key, JSON.stringify(value));
  }

  static sessionGet<T>(key: string, defaultValue?: T): T {
    const value = sessionStorage.getItem(key);
    if (!value) return defaultValue as T;

    try {
      return JSON.parse(value);
    } catch {
      // 파싱 실패 시 원본 문자열 반환
      return value as unknown as T;
    }
  }

  static sessionRemove(key: string): void {
    sessionStorage.removeItem(key);
  }

  /**
   * 스토리지 청소 유틸리티 방법
   */
  // 지정된 키의 스토리지 정리 (localStorage + sessionStorage)
  static clear(key: string): void {
    localStorage.removeItem(key);
    sessionStorage.removeItem(key);
  }

  // 대량 스토리지 청소
  static clearMultiple(keys: string[]): void {
    keys.forEach((key) => {
      localStorage.removeItem(key);
      sessionStorage.removeItem(key);
    });
  }

  // 지정된 접두사의 스토리지 정리
  static clearByPrefix(prefix: string): void {
    // localStorage 정리
    const localKeys = Object.keys(localStorage).filter((key) => key.startsWith(prefix));
    localKeys.forEach((key) => localStorage.removeItem(key));

    // sessionStorage 정리
    const sessionKeys = Object.keys(sessionStorage).filter((key) => key.startsWith(prefix));
    sessionKeys.forEach((key) => sessionStorage.removeItem(key));
  }

  /**
   * 프로젝트 특정 청소 편의 방법
   */
  // 모든 프로젝트 관련 스토리지 정리
  static clearAllProject(): void {
    const keys = Object.values(STORAGE_KEYS);
    this.clearMultiple(keys);
  }

  // 특정 카테고리의 스토리지 정리
  static clearByCategory(category: "auth" | "system" | "ui" | "app"): void {
    const prefix = `${APP_PREFIX}:${category}:`;
    this.clearByPrefix(prefix);
  }

  // 모든 프로젝트 관련 스토리지 키 가져오기
  static getAllProjectKeys(): string[] {
    return Object.values(STORAGE_KEYS);
  }
}
