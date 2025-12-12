package ksh.backendserver.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "API 공통 응답")
@Getter
@AllArgsConstructor
@Builder
public class ApiResponseDto<T> {

    @Schema(description = "HTTP 상태 코드", example = "200")
    private int status;

    @Schema(description = "응답 코드", example = "OK")
    private String code;

    @Schema(description = "응답 메시지", example = "조회 성공")
    private String message;

    @Schema(description = "응답 데이터")
    private T data;

    public static <T> ApiResponseDto<T> of(int status, String code, String message, T data) {
        return new ApiResponseDto<>(status, code, message, data);
    }
}
