package ksh.backendserver.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import ksh.backendserver.common.dto.request.MemberRegisterRequestDto;
import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.member.dto.request.LoginRequestDto;
import ksh.backendserver.member.entity.Member;
import ksh.backendserver.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원", description = "회원 인증 및 관리 API")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(
        summary = "로그인",
        description = "이메일과 비밀번호로 로그인하여 세션을 생성합니다. 세션 ID는 쿠키에 저장됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패 (이메일 형식 오류 등)"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회원이거나 비밀번호 불일치")
    })
    @PostMapping("/login")
    public ApiResponseDto<Void> login(
            @Valid @RequestBody LoginRequestDto dto,
            HttpServletRequest request
    ) {
        Member member = memberService.findLoginMember(
                dto.getEmail(),
                dto.getPassword()
        );

        HttpSession session = request.getSession();
        session.setAttribute("memberId", member.getId());

        return ApiResponseDto.of(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                "로그인 성공",
                null
        );
    }

    @Operation(
        summary = "로그아웃",
        description = "현재 세션을 무효화하여 로그아웃합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @ApiResponse(responseCode = "400", description = "로그인 상태가 아님")
    })
    @PostMapping("/logout")
    public ApiResponseDto<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (session == null) {
            return ApiResponseDto.of(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.name(),
                    "로그인 상태가 아닙니다.",
                    null
            );
        }

        session.invalidate();

        return ApiResponseDto.of(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                "로그아웃에 성공했습니다.",
                null
        );
    }


    @Operation(
        summary = "회원가입",
        description = "이름, 이메일, 비밀번호로 신규 회원을 등록합니다. 이메일 중복 검사 및 비밀번호 확인 검증을 수행합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "이메일 중복 또는 비밀번호 불일치")
    })
    @PostMapping("/members")
    public ApiResponseDto<Void> register(
            @Valid @RequestBody MemberRegisterRequestDto request
    ) {
        memberService.register(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getPasswordConfirm()
        );

        return ApiResponseDto.of(
                HttpStatus.CREATED.value(),
                HttpStatus.CREATED.name(),
                "회원가입이 완료되었습니다.",
                null
        );

    }
}
