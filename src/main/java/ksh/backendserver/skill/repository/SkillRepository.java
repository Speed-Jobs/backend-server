package ksh.backendserver.skill.repository;

import ksh.backendserver.skill.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByIsMajor(boolean isMajor);
}
