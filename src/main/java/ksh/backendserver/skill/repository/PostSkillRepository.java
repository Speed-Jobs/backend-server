package ksh.backendserver.skill.repository;

import ksh.backendserver.skill.entity.PostSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PostSkillRepository extends JpaRepository<PostSkill, Long>, PostSkillQueryRepository {

    long countByCreatedAtGreaterThanEqual(LocalDateTime baseTime);
}
