package ksh.backendserver.post.model;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.jobrole.entity.JobRole;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.skill.dto.projection.PostSkillWithSkill;
import ksh.backendserver.skill.entity.PostSkill;
import ksh.backendserver.skill.entity.Skill;
import ksh.backendserver.subscription.model.UserSubscription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MatchablePostTest {

    @Test
    @DisplayName("스킬, 직무 분야, 회사 모두 매칭되면 공고가 매칭된다")
    void allConditionsMatch() {
        // given
        Long companyId = 100L;
        Long jobFieldId = 5L;
        Long postSkillId = 1L;
        Long userSkillId1 = 1L;
        Long userSkillId2 = 99L;

        var matchablePost = createMatchablePost(companyId, jobFieldId, postSkillId);
        var subscription = UserSubscription.of(
            1L,
            Set.of(userSkillId1, userSkillId2),
            Set.of(jobFieldId),
            Set.of(companyId)
        );

        // when & then
        assertThat(matchablePost.matchesWith(subscription)).isTrue();
    }

    @Test
    @DisplayName("스킬이 하나도 겹치지 않으면 매칭 안 된다")
    void skillNotMatch() {
        // given
        Long companyId = 100L;
        Long jobFieldId = 5L;
        Long postSkillId1 = 1L;
        Long postSkillId2 = 2L;
        Long userSkillId1 = 98L;
        Long userSkillId2 = 99L;

        var matchablePost = createMatchablePost(companyId, jobFieldId, postSkillId1, postSkillId2);
        var subscription = UserSubscription.of(
            1L,
            Set.of(userSkillId1, userSkillId2),
            Set.of(jobFieldId),
            Set.of(companyId)
        );

        // when & then
        assertThat(matchablePost.matchesWith(subscription)).isFalse();
    }

    @Test
    @DisplayName("직무 분야가 다르면 매칭 안 된다")
    void jobFieldNotMatch() {
        // given
        Long companyId = 100L;
        Long postJobFieldId = 5L;
        Long userJobFieldId = 99L;
        Long skillId = 1L;

        var matchablePost = createMatchablePost(companyId, postJobFieldId, skillId);
        var subscription = UserSubscription.of(
            1L,
            Set.of(skillId),
            Set.of(userJobFieldId),
            Set.of(companyId)
        );

        // when & then
        assertThat(matchablePost.matchesWith(subscription)).isFalse();
    }

    @Test
    @DisplayName("회사가 다르면 매칭 안 된다")
    void companyNotMatch() {
        // given
        Long postCompanyId = 100L;
        Long userCompanyId = 999L;
        Long jobFieldId = 5L;
        Long skillId = 1L;

        var matchablePost = createMatchablePost(postCompanyId, jobFieldId, skillId);
        var subscription = UserSubscription.of(
            1L,
            Set.of(skillId),
            Set.of(jobFieldId),
            Set.of(userCompanyId)
        );

        // when & then
        assertThat(matchablePost.matchesWith(subscription)).isFalse();
    }

    @Test
    @DisplayName("스킬 구독이 없으면 스킬 조건을 만족한 것으로 간주된다")
    void emptySkillSubscription() {
        // given
        Long companyId = 100L;
        Long jobFieldId = 5L;
        Long skillId = 1L;

        var matchablePost = createMatchablePost(companyId, jobFieldId, skillId);
        var subscription = UserSubscription.of(
            1L,
            Collections.emptySet(),
            Set.of(jobFieldId),
            Set.of(companyId)
        );

        // when & then
        assertThat(matchablePost.matchesWith(subscription)).isTrue();
    }

    @Test
    @DisplayName("직무 분야 구독이 없으면 직무 조건을 만족한 것으로 간주된다")
    void emptyJobFieldSubscription() {
        // given
        Long companyId = 100L;
        Long jobFieldId = 5L;
        Long skillId = 1L;

        var matchablePost = createMatchablePost(companyId, jobFieldId, skillId);
        var subscription = UserSubscription.of(
            1L,
            Set.of(skillId),
            Collections.emptySet(),
            Set.of(companyId)
        );

        // when & then
        assertThat(matchablePost.matchesWith(subscription)).isTrue();
    }

    @Test
    @DisplayName("회사 구독이 없으면 회사 조건을 만족한 것으로 간주된다")
    void emptyCompanySubscription() {
        // given
        Long companyId = 100L;
        Long jobFieldId = 5L;
        Long skillId = 1L;

        var matchablePost = createMatchablePost(companyId, jobFieldId, skillId);
        var subscription = UserSubscription.of(
            1L,
            Set.of(skillId),
            Set.of(jobFieldId),
            Collections.emptySet()
        );

        // when & then
        assertThat(matchablePost.matchesWith(subscription)).isTrue();
    }

    private MatchablePost createMatchablePost(Long companyId, Long jobFieldId, Long... skillIds) {
        var post = Post.builder()
            .id(1L)
            .companyId(companyId)
            .jobRoleId(10L)
            .build();

        var company = Company.builder()
            .id(companyId)
            .build();

        var jobRole = JobRole.builder()
            .id(10L)
            .jobFieldId(jobFieldId)
            .build();

        var skills = Stream.of(skillIds)
            .map(skillId -> {
                var postSkill = PostSkill.builder()
                    .postId(1L)
                    .skillId(skillId)
                    .build();
                var skill = Skill.builder()
                    .id(skillId)
                    .build();
                return new PostSkillWithSkill(postSkill, skill);
            })
            .toList();

        return new MatchablePost(post, company, jobRole, skills);
    }
}
