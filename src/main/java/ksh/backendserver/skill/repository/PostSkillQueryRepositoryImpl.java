package ksh.backendserver.skill.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static ksh.backendserver.skill.entity.QPostSkill.postSkill;
import static ksh.backendserver.skill.entity.QSkill.skill;

@RequiredArgsConstructor
public class PostSkillQueryRepositoryImpl implements PostSkillQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findSkillNamesByPostId(Long postId) {
        return queryFactory
            .select(skill.name)
            .from(postSkill)
            .join(skill).on(postSkill.skillId.eq(skill.id))
            .where(postSkill.postId.eq(postId))
            .fetch();
    }
}