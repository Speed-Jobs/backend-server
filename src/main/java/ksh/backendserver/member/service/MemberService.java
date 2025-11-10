// 서비스 관련 비즈니스 로직을 여기에 구현
// 회원가입 코드
// 조회,이메일 중복처리 이메일로 멤버를 조회해서 있다면 예외발생
// 없다면 Member를 생성

package ksh.backendserver.member.service;

import ksh.backendserver.common.exception.CustomException;
import ksh.backendserver.common.exception.ErrorCode;
import ksh.backendserver.member.entity.Member;
import ksh.backendserver.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member findLoginMember(String email, String password) {
        return memberRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 회원가입
     */
    @Transactional
    public Member register(
            String name,
            String email,
            String password,
            String passwordConfirm
    ) {
        // 이메일 중복 검사
        if (memberRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.MEMBER_DUPLICATE_EMAIL);
        }

        // 비밀번호 일치 확인
        if (!password.equals(passwordConfirm)) {
            throw new CustomException(ErrorCode.MEMBER_PASSWORD_MISMATCH);
        }

        // Member 객체 생성
        Member member = Member.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        // DB 저장
        return memberRepository.save(member);
    }
}

