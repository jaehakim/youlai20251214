package com.youlai.boot.platform.ai.service;

import com.youlai.boot.platform.ai.model.dto.AiExecuteRequestDTO;
import com.youlai.boot.platform.ai.model.dto.AiParseRequestDTO;
import com.youlai.boot.platform.ai.model.dto.AiParseResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * AI 명령编排서비스：负责对外의解析与执行编排
 */
public interface AiCommandService {

	/**
	 * 자연어 명령 파싱
	 */
	AiParseResponseDTO parseCommand(AiParseRequestDTO request, HttpServletRequest httpRequest);

	/**
	 * 파싱된 명령 실행
	 * 
	 * @param request 执行请求
	 * @param httpRequest HTTP 请求
	 * @return 执行결과데이터（성공时返回）
	 * @throws Exception 执行실패时抛出오류
	 */
	Object executeCommand(AiExecuteRequestDTO request, HttpServletRequest httpRequest) throws Exception;
}





