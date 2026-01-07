package ksh.backendserver.subscription.model;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.jobrole.entity.JobRole;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.post.model.MatchablePost;
import ksh.backendserver.skill.dto.projection.PostSkillWithSkill;
import ksh.backendserver.skill.entity.PostSkill;
import ksh.backendserver.skill.entity.Skill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionMatcherTest {

    @Test
    @DisplayName("구독 여러 개가 각각 다른 공고와 매칭된다")
    void multipleSubscriptionsMatchDifferentPosts() {
        // given
        Long user1Id = 1L;
        Long user2Id = 2L;
        Long skillId1 = 10L;
        Long skillId2 = 20L;
        Long companyId = 100L;
        Long jobFieldId = 5L;

        var subscription1 = UserSubscription.of(user1Id, Set.of(skillId1), Set.of(jobFieldId), Set.of(companyId));
        var subscription2 = UserSubscription.of(user2Id, Set.of(skillId2), Set.of(jobFieldId), Set.of(companyId));
        var subscriptions = UserSubscriptions.of(List.of(subscription1, subscription2));

        var post1 = createMatchablePost(companyId, jobFieldId, skillId1);
        var post2 = createMatchablePost(companyId, jobFieldId, skillId1);
        var post3 = createMatchablePost(companyId, jobFieldId, skillId2);
        var posts = List.of(post1, post2, post3);

        // when
        var matches = SubscriptionMatcher.match(subscriptions, posts);

        // then
        assertThat(matches.isEmpty()).isFalse();
        var matchMap = matches.getMatches();
        assertThat(matchMap).hasSize(2);
        assertThat(matchMap.get(subscription1)).containsExactlyInAnyOrder(post1, post2);
        assertThat(matchMap.get(subscription2)).containsExactly(post3);
    }

    @Test
    @DisplayName("매칭되는 공고가 없는 구독은 결과에 포함되지 않는다")
    void subscriptionWithNoMatchesNotIncluded() {
        // given
        Long userId = 1L;
        Long userSkillId = 10L;
        Long postSkillId = 99L;
        Long companyId = 100L;
        Long jobFieldId = 5L;

        var subscription = UserSubscription.of(userId, Set.of(userSkillId), Set.of(jobFieldId), Set.of(companyId));
        var subscriptions = UserSubscriptions.of(List.of(subscription));

        var post = createMatchablePost(companyId, jobFieldId, postSkillId);
        var posts = List.of(post);

        // when
        var matches = SubscriptionMatcher.match(subscriptions, posts);

        // then
        assertThat(matches.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("빈 구독 목록은 빈 매칭 결과를 반환한다")
    void emptySubscriptionsReturnEmptyMatches() {
        // given
        var subscriptions = UserSubscriptions.of(Collections.emptyList());

        Long companyId = 100L;
        Long jobFieldId = 5L;
        Long skillId = 1L;
        var post = createMatchablePost(companyId, jobFieldId, skillId);
        var posts = List.of(post);

        // when
        var matches = SubscriptionMatcher.match(subscriptions, posts);

        // then
        assertThat(matches.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("빈 공고 목록은 빈 매칭 결과를 반환한다")
    void emptyPostsReturnEmptyMatches() {
        // given
        Long userId = 1L;
        Long skillId = 10L;
        Long jobFieldId = 5L;
        Long companyId = 100L;

        var subscription = UserSubscription.of(userId, Set.of(skillId), Set.of(jobFieldId), Set.of(companyId));
        var subscriptions = UserSubscriptions.of(List.of(subscription));
        var posts = Collections.<MatchablePost>emptyList();

        // when
        var matches = SubscriptionMatcher.match(subscriptions, posts);

        // then
        assertThat(matches.isEmpty()).isTrue();
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