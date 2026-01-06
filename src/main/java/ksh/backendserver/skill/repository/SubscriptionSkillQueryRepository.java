package ksh.backendserver.skill.repository;

import java.util.List;

public interface SubscriptionSkillQueryRepository {
    List<String> findSkillNamesByUserId(Long userId);
}