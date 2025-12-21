package com.youlai.boot.core.exception;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.youlai.boot.core.web.Result;
import com.youlai.boot.core.web.ResultCode;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 전역 시스템 예외 핸들러
 * <p>
 * 예외 처리의 HTTP 상태 코드 조정, 예외 처리 유형 확장
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 바인딩 예외 처리
     * <p>
     * 요청 파라미터를 객체에 바인딩할 때 오류가 발생하면 BindException 예외가 발생합니다.
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(BindException e) {
        log.error("BindException:{}", e.getMessage());
        String msg = e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("；"));
        return Result.failed(ResultCode.USER_REQUEST_PARAMETER_ERROR, msg);
    }

    /**
     * @RequestParam 파라미터 검증 예외 처리
     * <p>
     * 요청 파라미터 검증 과정에서 제약 조건 위반 예외가 발생할 때 (@RequestParam 검증 실패 시),
     * ConstraintViolationException 예외가 캐치됩니다.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(ConstraintViolationException e) {
        log.error("ConstraintViolationException:{}", e.getMessage());
        String msg = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("；"));
        return Result.failed(ResultCode.INVALID_USER_INPUT, msg);
    }

    /**
     * 메서드 파라미터 검증 예외 처리
     * <p>
     * @Valid 또는 @Validated 어노테이션을 사용하여 메서드 파라미터를 검증할 때 검증이 실패하면,
     * MethodArgumentNotValidException 예외가 발생합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException:{}", e.getMessage());
        String msg = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("；"));
        return Result.failed(ResultCode.INVALID_USER_INPUT, msg);
    }

    /**
     * 인터페이스가 존재하지 않는 예외 처리
     * <p>
     * 클라이언트가 존재하지 않는 경로를 요청할 때 NoHandlerFoundException 예외가 발생합니다.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public <T> Result<T> processException(NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.INTERFACE_NOT_EXIST);
    }

    /**
     * 요청 파라미터 누락 예외 처리
     * <p>
     * 요청에 필수 파라미터가 누락되었을 때 MissingServletRequestParameterException 예외가 발생합니다.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
    }

    /**
     * 메서드 파라미터 타입 불일치 예외 처리
     * <p>
     * 요청 파라미터 타입이 일치하지 않을 때 MethodArgumentTypeMismatchException 예외가 발생합니다.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.PARAMETER_FORMAT_MISMATCH, "타입 오류");
    }

    /**
     * Servlet 예외 처리
     * <p>
     * Servlet이 요청을 처리할 때 예외가 발생하면 ServletException 예외가 발생합니다.
     */
    @ExceptionHandler(ServletException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(ServletException e) {
        log.error(e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    /**
     * 비정상 파라미터 예외 처리
     * <p>
     * 메서드가 비정상 파라미터를 받았을 때 IllegalArgumentException 예외가 발생합니다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("비정상 파라미터 예외, 예외 원인: {}", e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    /**
     * JSON 처리 예외 처리
     * <p>
     * JSON 데이터 처리 중 오류가 발생하면 JsonProcessingException 예외가 발생합니다.
     */
    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleJsonProcessingException(JsonProcessingException e) {
        log.error("JSON 변환 예외, 예외 원인: {}", e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    /**
     * 요청 본문을 읽을 수 없는 예외 처리
     * <p>
     * 요청 본문을 읽을 수 없을 때 HttpMessageNotReadableException 예외가 발생합니다.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        String errorMessage = "요청 본문은 비어 있을 수 없습니다";
        Throwable cause = e.getCause();
        if (cause != null) {
            errorMessage = convertMessage(cause);
        }
        return Result.failed(errorMessage);
    }

    /**
     * 타입 불일치 예외 처리
     * <p>
     * 메서드 파라미터 타입이 일치하지 않을 때 TypeMismatchException 예외가 발생합니다.
     */
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(TypeMismatchException e) {
        log.error(e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    /**
     * SQL 구문 오류 예외 처리
     * <p>
     * SQL 구문 오류가 발생하면 BadSqlGrammarException 예외가 발생합니다.
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public <T> Result<T> handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error(e.getMessage(), e);
        String errorMsg = e.getMessage();
        if (StrUtil.isNotBlank(errorMsg) && errorMsg.contains("denied to user")) {
            return Result.failed(ResultCode.DATABASE_ACCESS_DENIED);
        } else {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * SQL 구문 오류 예외 처리
     * <p>
     * SQL 구문 오류가 발생하면 SQLSyntaxErrorException 예외가 발생합니다.
     */
    @ExceptionHandler(SQLSyntaxErrorException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public <T> Result<T> processSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.DATABASE_EXECUTION_SYNTAX_ERROR);
    }


    /**
     * SQL 무결성 제약 조건 위반 처리
     * <p>
     * SQL이 무결성 제약 조건을 위반하면 SQLIntegrityConstraintViolationException 예외가 발생합니다.
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public <T> Result<T> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.INTEGRITY_CONSTRAINT_VIOLATION);
    }

    /**
     * 비즈니스 예외 처리
     * <p>
     * 비즈니스 로직에서 오류가 발생하면 BusinessException 예외가 발생합니다.
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleBizException(BusinessException e) {
        log.error("biz exception", e);
        if (e.getResultCode() != null) {
            return Result.failed(e.getResultCode(), e.getMessage());
        }
        return Result.failed(e.getMessage());
    }

    /**
     * 캐치되지 않은 모든 예외 처리
     * <p>
     * 캐치되지 않은 예외가 발생하면 Exception 예외가 발생합니다.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleException(Exception e) throws Exception {
        // Spring Security 예외는 계속 throw하여 사용자 정의 핸들러가 처리하도록 함
        if (e instanceof AccessDeniedException
                || e instanceof AuthenticationException) {
            throw e;
        }
        log.error("unknown exception", e);
        return Result.failed(e.getLocalizedMessage());
    }

    /**
     * 파라미터 타입 오류 시 메시지 변환에 사용
     *
     * @param throwable 예외
     * @return 오류 정보
     */
    private String convertMessage(Throwable throwable) {
        String error = throwable.toString();
        String regulation = "\\[\"(.*?)\"]+";
        Pattern pattern = Pattern.compile(regulation);
        Matcher matcher = pattern.matcher(error);
        String group = "";
        if (matcher.find()) {
            String matchString = matcher.group();
            matchString = matchString.replace("[", "").replace("]", "");
            matchString = "%s 필드 타입 오류".formatted(matchString.replaceAll("\"", ""));
            group += matchString;
        }
        return group;
    }
}