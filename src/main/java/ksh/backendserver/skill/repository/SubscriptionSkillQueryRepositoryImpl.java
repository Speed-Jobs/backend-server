package ksh.backendserver.skill.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static ksh.backendserver.skill.entity.QSkill.skill;
import static ksh.backendserver.skill.entity.QSubscriptionSkill.subscriptionSkill;

@RequiredArgsConstructor
public class SubscriptionSkillQueryRepositoryImpl implements SubscriptionSkillQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findSkillNamesByUserId(Long userId) {
        return queryFactory
            .select(skill.name)
            .from(subscriptionSkill)
            .join(skill).on(subscriptionSkill.skillId.eq(skill.id))
            .where(subscriptionSkill.userId.eq(userId))
            .fetch();
    }
}