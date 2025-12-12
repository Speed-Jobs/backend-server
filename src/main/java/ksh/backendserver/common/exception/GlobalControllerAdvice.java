package ksh.backendserver.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import ksh.backendserver.common.dto.response.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public ApiResponseDto<Object> bindException(BindException e) {
        String errorMessage = e.getBindingResult()
            .getAllErrors()
            .get(0)
            .getDefaultMessage();

        return ApiResponseDto.of(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.name(),
            errorMessage,
            null
        );
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponseDto> handleCustomException(CustomException e, Locale locale) {
        ErrorCode errorCode = e.getErrorCode();
        String message = messageSource.getMessage(
            errorCode.getMessageKey(),
            e.getMessageArgs().toArray(),
            locale
        );

        log.info("예외 정보 : {} {}", errorCode.name(), message);

        ApiResponseDto response = ApiResponseDto.of(
            errorCode.getStatus(),
            errorCode.name(),
            message,
            null
        );

        return ResponseEntity
            .status(errorCode.getStatus())
            .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto> handleException(Exception e, HttpServletRequest request, Locale locale) {
        log.error(
            "예외정보 uri={} method={} error={}",
            request.getRequestURI(),
            request.getMethod(),
            e.getMessage(),
            e
        );

        ApiResponseDto response = ApiResponseDto.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.name(),
            e.getMessage(),
            null
        );

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }
}
