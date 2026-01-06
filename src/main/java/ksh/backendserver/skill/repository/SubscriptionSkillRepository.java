package ksh.backendserver.skill.repository;

import ksh.backendserver.skill.entity.SubscriptionSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionSkillRepository extends JpaRepository<SubscriptionSkill, Long>, SubscriptionSkillQueryRepository {
    List<SubscriptionSkill> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
