package ksh.backendserver.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

// bean validation을 활용해서 입력값 검증
@Schema(description = "회원가입 요청")
@Getter
@NoArgsConstructor
public class MemberRegisterRequestDto {

    @Schema(description = "사용자 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @Schema(description = "사용자 이메일", example = "hong@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @Schema(description = "비밀번호", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

    @Schema(description = "비밀번호 확인", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호 확인은 필수 입력값입니다.")
    private String passwordConfirm;
}
