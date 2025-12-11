package ksh.backendserver.skill.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ksh.backendserver.company.enums.DateRange;
import ksh.backendserver.skill.dto.projection.PostSkillWithSkill;
import ksh.backendserver.skill.dto.projection.SkillWithCount;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ksh.backendserver.post.entity.QPost.post;
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
            .where(
                postSkill.postId.eq(postId),
                postSkill.isDeleted.isFalse()
            )
            .fetch();
    }

    @Override
    public List<SkillWithCount> findTopSkillOrderByCountDesc(
        int size,
        DateRange dateRange,
        LocalDate end
    ) {
        LocalDate start = end.minusDays(dateRange.getDuration());

        return queryFactory
            .select(Projections.constructor(
                SkillWithCount.class,
                skill,
                postSkill.skillId.count()
            ))
            .from(postSkill)
            .join(skill).on(postSkill.skillId.eq(skill.id))
            .join(post).on(postSkill.postId.eq(post.id))
            .where(
                post.postedAt.goe(start.atStartOfDay()),
                post.postedAt.lt(end.atStartOfDay()),
                postSkill.isDeleted.isFalse()
            )
            .groupBy(postSkill.skillId)
            .orderBy(
                postSkill.skillId.count().desc(),
                skill.name.asc()
            )
            .limit(size)
            .fetch();
    }

    @Override
    public Long countBySkillIdSince(
        long skillId,
        LocalDateTime baseTime
    ) {
        return queryFactory
            .select(postSkill.postId.count())
            .from(postSkill)
            .join(skill).on(postSkill.skillId.eq(skill.id))
            .join(post).on(postSkill.postId.eq(post.id))
            .where(
                post.postedAt.goe(baseTime),
                skill.id.eq(skillId),
                postSkill.isDeleted.isFalse()
            )
            .fetchOne();
    }

    @Override
    public Long countBySkillIdBetween(
        long skillId,
        LocalDateTime start,
        LocalDateTime endExclusive
    ) {
        return queryFactory
            .select(postSkill.postId.count())
            .from(postSkill)
            .join(skill).on(postSkill.skillId.eq(skill.id))
            .join(post).on(postSkill.postId.eq(post.id))
            .where(
                post.postedAt.goe(start),
                post.postedAt.lt(endExclusive),
                skill.id.eq(skillId),
                postSkill.isDeleted.isFalse()
            )
            .fetchOne();
    }

    @Override
    public List<PostSkillWithSkill> findWithSkillByPostIdIn(List<Long> postIds) {
        return queryFactory
            .select(Projections.constructor(
                PostSkillWithSkill.class,
                postSkill,
                skill
            ))
            .from(postSkill)
            .join(skill).on(postSkill.skillId.eq(skill.id))
            .join(post).on(postSkill.postId.eq(post.id))
            .where(
                post.id.in(postIds),
                postSkill.isDeleted.isFalse()
            )
            .fetch();
    }
}
