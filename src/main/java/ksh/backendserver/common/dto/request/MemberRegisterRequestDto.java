package ksh.backendserver.common.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

// bean validation을 활용해서 입력값 검증
@Getter
@NoArgsConstructor
public class MemberRegisterRequestDto {

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력값입니다.")
    private String passwordConfirm;
}
