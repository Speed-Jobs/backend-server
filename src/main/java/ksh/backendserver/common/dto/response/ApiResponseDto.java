package ksh.backendserver.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponseDto<T> {

    private int status;
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponseDto of(int status, String code, String message, T data) {
        return new ApiResponseDto(status, code, message, data);
    }
}
