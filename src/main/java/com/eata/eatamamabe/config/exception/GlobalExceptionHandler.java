package com.eata.eatamamabe.config.exception;

import com.eata.eatamamabe.dto.common.Response;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------- Custom ----------
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Response<Void>> handleCustom(CustomException ex) {
        log.warn("[CustomException] code={}, msg={}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus())
                .body(Response.fail(ex.getErrorCode(), ex.getMessage()));
    }

    // ---------- 400 Bad Request ----------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("입력값을 확인해주세요.");
        return badRequest("REQ.VALIDATION", msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .findFirst()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .orElse("요청 파라미터 유효성 검사 실패");
        return badRequest("REQ.VALIDATION", msg);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = String.format("파라미터 '%s' 타입이 올바르지 않습니다.", ex.getName());
        return badRequest("REQ.TYPE_MISMATCH", msg);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Response<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        return badRequest("REQ.MISSING_PARAM", "필수 파라미터 누락: " + ex.getParameterName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<Void>> handleInvalidJson(HttpMessageNotReadableException ex) {
        // Jackson 상세는 숨기고, InvalidFormat 정도만 친절 메시지
        if (ex.getCause() instanceof InvalidFormatException ife) {
            String msg = "요청 본문 형식이 올바르지 않습니다.";
            log.warn("[InvalidFormat] {}", ife.getOriginalMessage());
            return badRequest("REQ.INVALID_JSON", msg);
        }
        return badRequest("REQ.INVALID_JSON", "요청 본문이 올바른 JSON 형식이 아닙니다.");
    }

    // ---------- 401 / 403 ----------
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.fail("AUTH.FORBIDDEN", "접근 권한이 없습니다."));
    }

    // ---------- 405 / 415 ----------
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Response.fail("REQ.METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다."));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(Response.fail("REQ.UNSUPPORTED_MEDIA_TYPE", "지원하지 않는 Content-Type 입니다."));
    }

    // ---------- 409 ----------
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Response<Void>> handleDuplicate(DuplicateKeyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Response.fail("DATA.DUPLICATE", "이미 존재하는 데이터입니다."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("[DataIntegrityViolation] {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Response.fail("DATA.CONSTRAINT", "데이터 제약 조건을 위반했습니다."));
    }

    // ---------- 500 ----------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleAll(Exception ex) {
        // 내부 메시지는 숨기고, 로그만 자세히
        log.error("[Unhandled] {}: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }

    private ResponseEntity<Response<Void>> badRequest(String code, String msg) {
        return ResponseEntity.badRequest().body(Response.fail(code, msg));
    }
}