package ksh.backendserver.notification.facade;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.jobrole.entity.JobRole;
import ksh.backendserver.notification.service.NotificationService;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.post.model.MatchablePost;
import ksh.backendserver.post.service.MatchablePostService;
import ksh.backendserver.skill.dto.projection.PostSkillWithSkill;
import ksh.backendserver.skill.entity.PostSkill;
import ksh.backendserver.skill.entity.Skill;
import ksh.backendserver.subscription.model.SubscriptionMatches;
import ksh.backendserver.subscription.model.UserSubscription;
import ksh.backendserver.subscription.service.SubscriptionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationFacadeTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private MatchablePostService matchablePostService;

    @InjectMocks
    private NotificationFacade notificationFacade;

    @Test
    @DisplayName("새 공고에 매칭되는 구독이 있으면 알림을 전송한다")
    void notifyNewPost_매칭있음_알림전송() {
        // given
        Long postId = 1L;
        MatchablePost matchablePost = createMatchablePost(postId);
        SubscriptionMatches matches = SubscriptionMatches.empty();
        matches.addMatch(
            UserSubscription.of(1L, java.util.Set.of(1L), java.util.Set.of(1L), java.util.Set.of(1L)),
            List.of(matchablePost)
        );

        given(matchablePostService.findMatchablePostById(postId)).willReturn(matchablePost);
        given(subscriptionService.findMatchingSubscription(List.of(matchablePost))).willReturn(matches);

        // when
        notificationFacade.notifyNewPost(postId);

        // then
        verify(notificationService).sendNotifications(matches);
    }

    @Test
    @DisplayName("새 공고에 매칭되는 구독이 없으면 알림을 전송하지 않는다")
    void notifyNewPost_매칭없음_알림미전송() {
        // given
        Long postId = 1L;
        MatchablePost matchablePost = createMatchablePost(postId);
        SubscriptionMatches emptyMatches = SubscriptionMatches.empty();

        given(matchablePostService.findMatchablePostById(postId)).willReturn(matchablePost);
        given(subscriptionService.findMatchingSubscription(List.of(matchablePost))).willReturn(emptyMatches);

        // when
        notificationFacade.notifyNewPost(postId);

        // then
        verify(notificationService, never()).sendNotifications(any());
    }

    private MatchablePost createMatchablePost(Long postId) {
        Post post = Post.builder()
            .id(postId)
            .title("백엔드 개발자")
            .companyId(1L)
            .jobRoleId(1L)
            .build();

        Company company = Company.builder()
            .id(1L)
            .name("테스트 회사")
            .build();

        JobRole jobRole = JobRole.builder()
            .id(1L)
            .name("백엔드 개발")
            .jobFieldId(1L)
            .build();

        PostSkill postSkill = PostSkill.builder()
            .postId(postId)
            .skillId(1L)
            .build();

        Skill skill = Skill.builder()
            .id(1L)
            .name("Java")
            .build();

        List<PostSkillWithSkill> skills = List.of(new PostSkillWithSkill(postSkill, skill));

        return new MatchablePost(post, company, jobRole, skills);
    }
}