package ksh.backendserver.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.member.dto.request.LoginRequestDto;
import ksh.backendserver.member.entity.Member;
import ksh.backendserver.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

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
}
