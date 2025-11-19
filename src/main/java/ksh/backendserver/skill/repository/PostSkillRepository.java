package ksh.backendserver.skill.repository;

import ksh.backendserver.skill.entity.PostSkill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostSkillRepository extends JpaRepository<PostSkill, Long>, PostSkillQueryRepository {
}
