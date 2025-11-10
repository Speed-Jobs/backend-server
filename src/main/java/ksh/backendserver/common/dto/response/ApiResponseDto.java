package ksh.backendserver.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ApiResponseDto<T> {

    private int status;
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponseDto<T> of(int status, String code, String message, T data) {
        return new ApiResponseDto<>(status, code, message, data);
    }
}
