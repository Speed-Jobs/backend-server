package ksh.backendserver.member.repository;
// 회원관리 저장, 조회하는 레파지토리

import ksh.backendserver.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 값을 저장, 중복처리를 위한 조회
// 쿼리를 안짜도됨 springdataJPA를 활용해서 기능구현하는것을 복표로
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmailAndPassword(String email, String password);

    // 이메일 존재 여부 (중복 체크)
    boolean existsByEmail(String email);
}
