package com.youlai.boot.platform.ai.service;

import com.youlai.boot.platform.ai.model.dto.AiExecuteRequestDTO;
import com.youlai.boot.platform.ai.model.dto.AiParseRequestDTO;
import com.youlai.boot.platform.ai.model.dto.AiParseResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * AI 명령 오케스트레이션 서비스: 외부 파싱 및 실행 오케스트레이션 담당
 */
public interface AiCommandService {

	/**
	 * 자연어 명령 파싱
	 */
	AiParseResponseDTO parseCommand(AiParseRequestDTO request, HttpServletRequest httpRequest);

	/**
	 * 파싱된 명령 실행
	 *
	 * @param request 실행 요청
	 * @param httpRequest HTTP 요청
	 * @return 실행 결과 데이터 (성공 시 반환)
	 * @throws Exception 실행 실패 시 예외 발생
	 */
	Object executeCommand(AiExecuteRequestDTO request, HttpServletRequest httpRequest) throws Exception;
}





