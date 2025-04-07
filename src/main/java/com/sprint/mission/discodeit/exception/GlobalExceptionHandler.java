package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DiscodeitException.class)
    public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
        int status= mapToStatus(e.getErrorCode());
        ErrorResponse error = new ErrorResponse(
                e.getErrorCode().toString(),
                e.getErrorCode().getMessage(),
                e.getDetails(),
                e.getClass().getTypeName(),
                status
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String,Object> details= e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        ErrorResponse error = new ErrorResponse(
                "VALIDATION_FAILED",
                "요청 값 검증 실패하였습니다.",
                details,
                e.getClass().getTypeName(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse error = new ErrorResponse(
                "EXCEPTION_500",
                "예기치 않은 오류가 발생했습니다.",
                Collections.emptyMap(),
                e.getClass().getTypeName(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private int mapToStatus(ErrorCode errorCode) {
        switch (errorCode){
            case USER_NOT_FOUND:
            case CHANNEL_NOT_FOUND:
            case FILE_NOT_FOUND:
            case LOGIN_USER_NOT_FOUND:
            case MESSAGE_NOT_FOUND:
            case READSTATUS_NOT_FOUND:
            case USER_STATUS_NOT_FOUND:
                return HttpStatus.NOT_FOUND.value();

            case USER_ALREADY_EXISTS:
            case LOGIN_PASSWORD_WRONG:
            case READSTATUS_ALREADY_EXISTS:
            case USER_STATUS_ALREADY_EXISTS:
            case CHANNEL_UPDATE_FORBIDDEN:
                return HttpStatus.BAD_REQUEST.value();

            default:
                return HttpStatus.INTERNAL_SERVER_ERROR.value();

        }
    }
}
