package ksh.backendserver.notification.service;

import ksh.backendserver.common.exception.CustomException;
import ksh.backendserver.common.exception.ErrorCode;
import ksh.backendserver.company.entity.Company;
import ksh.backendserver.jobrole.entity.JobRole;
import ksh.backendserver.member.entity.Member;
import ksh.backendserver.member.repository.MemberRepository;
import ksh.backendserver.notification.entity.NotificationPreference;
import ksh.backendserver.notification.enums.NotificationType;
import ksh.backendserver.notification.repository.NotificationPreferenceRepository;
import ksh.backendserver.notification.strategy.NotificationStrategy;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.post.model.MatchablePost;
import ksh.backendserver.skill.dto.projection.PostSkillWithSkill;
import ksh.backendserver.skill.entity.PostSkill;
import ksh.backendserver.skill.entity.Skill;
import ksh.backendserver.subscription.model.SubscriptionMatches;
import ksh.backendserver.subscription.model.UserSubscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    
    private static final Long TEST_COMPANY_ID = 1L;
    private static final String TEST_COMPANY_NAME = "테스트회사";
    private static final Long BACKEND_JOB_ROLE_ID = 1L;
    private static final String BACKEND_JOB_ROLE_NAME = "백엔드";
    private static final Long BACKEND_JOB_FIELD_ID = 1L;
    private static final Long JAVA_SKILL_ID = 1L;
    private static final String JAVA_SKILL_NAME = "Java";
    private static final Long TEST_POST_ID = 1L;

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private NotificationStrategy emailStrategy;

    @Mock
    private NotificationStrategy slackStrategy;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        given(emailStrategy.getNotificationType()).willReturn(NotificationType.EMAIL);
        given(slackStrategy.getNotificationType()).willReturn(NotificationType.SLACK);

        List<NotificationStrategy> strategies = List.of(emailStrategy, slackStrategy);
        notificationService = new NotificationService(strategies, notificationPreferenceRepository, memberRepository);
    }

    @Test
    @DisplayName("단일 구독에 매칭된 공고가 있을 때 알림을 전송한다")
    void sendNotifications_singleSubscriptionWithMatches_success() {
        // Given
        long userId = 1L;
        long preferenceId = 1L;
        var member = createMember(userId, "test@example.com", "테스트사용자");
        var matchedPosts = List.of(createMatchablePost());
        var subscription = createSubscription(userId, JAVA_SKILL_ID, BACKEND_JOB_FIELD_ID, TEST_COMPANY_ID);
        var subscriptionMatches = SubscriptionMatches.empty();
        subscriptionMatches.addMatch(subscription, matchedPosts);

        var emailPreference = createNotificationPreference(preferenceId, userId, NotificationType.EMAIL);

        given(memberRepository.findById(userId)).willReturn(Optional.of(member));
        given(notificationPreferenceRepository.findByMemberId(userId))
            .willReturn(List.of(emailPreference));

        // When
        notificationService.sendNotifications(subscriptionMatches);

        // Then
        verify(emailStrategy).send(userId, "test@example.com", matchedPosts);
    }

    @Test
    @DisplayName("여러 구독에 매칭된 공고가 있을 때 각각 알림을 전송한다")
    void sendNotifications_multipleSubscriptionsWithMatches_success() {
        // Given
        long userId1 = 1L;
        long userId2 = 2L;
        long preferenceId1 = 1L;
        long preferenceId2 = 2L;

        var member1 = createMember(userId1, "test1@example.com", "테스트사용자1");
        var member2 = createMember(userId2, "test2@example.com", "테스트사용자2");
        var matchedPosts = List.of(createMatchablePost());

        var subscription1 = createSubscription(userId1, JAVA_SKILL_ID, BACKEND_JOB_FIELD_ID, TEST_COMPANY_ID);
        var subscription2 = createSubscription(userId2, JAVA_SKILL_ID, BACKEND_JOB_FIELD_ID, TEST_COMPANY_ID);
        var subscriptionMatches = SubscriptionMatches.empty();
        subscriptionMatches.addMatch(subscription1, matchedPosts);
        subscriptionMatches.addMatch(subscription2, matchedPosts);

        var emailPreference1 = createNotificationPreference(preferenceId1, userId1, NotificationType.EMAIL);
        var slackPreference2 = createNotificationPreference(preferenceId2, userId2, NotificationType.SLACK);

        given(memberRepository.findById(userId1)).willReturn(Optional.of(member1));
        given(memberRepository.findById(userId2)).willReturn(Optional.of(member2));
        given(notificationPreferenceRepository.findByMemberId(userId1))
            .willReturn(List.of(emailPreference1));
        given(notificationPreferenceRepository.findByMemberId(userId2))
            .willReturn(List.of(slackPreference2));

        // When
        notificationService.sendNotifications(subscriptionMatches);

        // Then
        verify(emailStrategy).send(userId1, "test1@example.com", matchedPosts);
        verify(slackStrategy).send(userId2, "test2@example.com", matchedPosts);
    }

    @Test
    @DisplayName("빈 구독 매칭일 때 알림을 전송하지 않는다")
    void sendNotifications_emptyMatches_noNotificationSent() {
        // Given
        var subscriptionMatches = SubscriptionMatches.empty();

        // When
        notificationService.sendNotifications(subscriptionMatches);

        // Then
        verify(emailStrategy, never()).send(anyLong(), anyString(), anyList());
        verify(slackStrategy, never()).send(anyLong(), anyString(), anyList());
    }

    @Test
    @DisplayName("한 사용자가 여러 알림 타입을 설정했을 때 모두 전송된다")
    void sendNotifications_multipleNotificationTypes_allSent() {
        // Given
        long userId = 1L;
        long emailPreferenceId = 1L;
        long slackPreferenceId = 2L;

        var member = createMember(userId, "test@example.com", "테스트사용자");
        var matchedPosts = List.of(createMatchablePost());
        var subscription = createSubscription(userId, JAVA_SKILL_ID, BACKEND_JOB_FIELD_ID, TEST_COMPANY_ID);
        var subscriptionMatches = SubscriptionMatches.empty();
        subscriptionMatches.addMatch(subscription, matchedPosts);

        var emailPreference = createNotificationPreference(emailPreferenceId, userId, NotificationType.EMAIL);
        var slackPreference = createNotificationPreference(slackPreferenceId, userId, NotificationType.SLACK);

        given(memberRepository.findById(userId)).willReturn(Optional.of(member));
        given(notificationPreferenceRepository.findByMemberId(userId))
            .willReturn(List.of(emailPreference, slackPreference));

        // When
        notificationService.sendNotifications(subscriptionMatches);

        // Then
        verify(emailStrategy).send(userId, "test@example.com", matchedPosts);
        verify(slackStrategy).send(userId, "test@example.com", matchedPosts);
    }

    @Test
    @DisplayName("존재하지 않는 회원일 때 예외가 발생한다")
    void sendNotifications_memberNotFound_throwsException() {
        // Given
        long nonExistentUserId = 999L;
        var matchedPosts = List.of(createMatchablePost());
        var subscription = createSubscription(nonExistentUserId, JAVA_SKILL_ID, BACKEND_JOB_FIELD_ID, TEST_COMPANY_ID);
        var subscriptionMatches = SubscriptionMatches.empty();
        subscriptionMatches.addMatch(subscription, matchedPosts);

        given(memberRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> notificationService.sendNotifications(subscriptionMatches))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("회원의 알림 설정이 없을 때 알림을 전송하지 않는다")
    void sendNotifications_noPreferences_noNotificationSent() {
        // Given
        long userId = 1L;
        var member = createMember(userId, "test@example.com", "테스트사용자");
        var matchedPosts = List.of(createMatchablePost());
        var subscription = createSubscription(userId, JAVA_SKILL_ID, BACKEND_JOB_FIELD_ID, TEST_COMPANY_ID);
        var subscriptionMatches = SubscriptionMatches.empty();
        subscriptionMatches.addMatch(subscription, matchedPosts);

        given(memberRepository.findById(userId)).willReturn(Optional.of(member));
        given(notificationPreferenceRepository.findByMemberId(userId))
            .willReturn(Collections.emptyList());

        // When
        notificationService.sendNotifications(subscriptionMatches);

        // Then
        verify(emailStrategy, never()).send(anyLong(), anyString(), anyList());
        verify(slackStrategy, never()).send(anyLong(), anyString(), anyList());
    }

    @Test
    @DisplayName("즉시 알림이 활성화된 사용자만 즉시 알림을 받는다")
    void sendNotifications_instant_onlyEnabledUserReceivesNotification() {
        // Given
        long userId1 = 1L;
        long userId2 = 2L;
        long preference1Id = 1L;
        long preference2Id = 2L;

        var member1 = createMember(userId1, "user1@example.com", "사용자1");
        var member2 = createMember(userId2, "user2@example.com", "사용자2");
        var matchedPosts = List.of(createMatchablePost());

        var subscription1 = createSubscription(userId1, JAVA_SKILL_ID, BACKEND_JOB_FIELD_ID, TEST_COMPANY_ID);
        var subscription2 = createSubscription(userId2, JAVA_SKILL_ID, BACKEND_JOB_FIELD_ID, TEST_COMPANY_ID);
        var subscriptionMatches = SubscriptionMatches.empty();
        subscriptionMatches.addMatch(subscription1, matchedPosts);
        subscriptionMatches.addMatch(subscription2, matchedPosts);

        var preference1WithInstant = createNotificationPreference(preference1Id, userId1, NotificationType.EMAIL, true);
        var preference2WithoutInstant = createNotificationPreference(preference2Id, userId2, NotificationType.EMAIL, false);

        given(memberRepository.findById(userId1)).willReturn(Optional.of(member1));
        given(memberRepository.findById(userId2)).willReturn(Optional.of(member2));
        given(notificationPreferenceRepository.findByMemberId(userId1))
            .willReturn(List.of(preference1WithInstant));
        given(notificationPreferenceRepository.findByMemberId(userId2))
            .willReturn(List.of(preference2WithoutInstant));

        // When
        notificationService.sendNotifications(subscriptionMatches, true);

        // Then
        verify(emailStrategy).send(userId1, "user1@example.com", matchedPosts);
        verify(emailStrategy, never()).send(eq(userId2), anyString(), anyList());
    }

    @Test
    @DisplayName("즉시 알림이 비활성화된 사용자는 즉시 알림을 받지 않는다")
    void sendNotifications_instant_noEnabledPreferences_noNotificationSent() {
        // Given
        long userId = 1L;
        long emailPreferenceId = 1L;

        var member = createMember(userId, "test@example.com", "테스트사용자");
        var matchedPosts = List.of(createMatchablePost());
        var subscription = createSubscription(userId, JAVA_SKILL_ID, BACKEND_JOB_FIELD_ID, TEST_COMPANY_ID);
        var subscriptionMatches = SubscriptionMatches.empty();
        subscriptionMatches.addMatch(subscription, matchedPosts);

        var emailPreferenceWithoutInstant = createNotificationPreference(emailPreferenceId, userId, NotificationType.EMAIL, false);

        given(memberRepository.findById(userId)).willReturn(Optional.of(member));
        given(notificationPreferenceRepository.findByMemberId(userId))
            .willReturn(List.of(emailPreferenceWithoutInstant));

        // When
        notificationService.sendNotifications(subscriptionMatches, true);

        // Then
        verify(emailStrategy, never()).send(anyLong(), anyString(), anyList());
    }

    @Test
    @DisplayName("일반 알림일 때는 enableInstant 설정과 무관하게 모든 사용자가 알림을 받는다")
    void sendNotifications_nonInstant_allUsersReceiveNotification() {
        // Given
        long userId1 = 1L;
        long userId2 = 2L;
        long preference1Id = 1L;
        long preference2Id = 2L;

        var member1 = createMember(userId1, "user1@example.com", "사용자1");
        var member2 = createMember(userId2, "user2@example.com", "사용자2");
        var matchedPosts = List.of(createMatchablePost());

        var subscription1 = createSubscription(userId1, JAVA_SKILL_ID, BACKEND_JOB_FIELD_ID, TEST_COMPANY_ID);
        var subscription2 = createSubscription(userId2, JAVA_SKILL_ID, BACKEND_JOB_FIELD_ID, TEST_COMPANY_ID);
        var subscriptionMatches = SubscriptionMatches.empty();
        subscriptionMatches.addMatch(subscription1, matchedPosts);
        subscriptionMatches.addMatch(subscription2, matchedPosts);

        var preference1WithInstant = createNotificationPreference(preference1Id, userId1, NotificationType.EMAIL, true);
        var preference2WithoutInstant = createNotificationPreference(preference2Id, userId2, NotificationType.EMAIL, false);

        given(memberRepository.findById(userId1)).willReturn(Optional.of(member1));
        given(memberRepository.findById(userId2)).willReturn(Optional.of(member2));
        given(notificationPreferenceRepository.findByMemberId(userId1))
            .willReturn(List.of(preference1WithInstant));
        given(notificationPreferenceRepository.findByMemberId(userId2))
            .willReturn(List.of(preference2WithoutInstant));

        // When
        notificationService.sendNotifications(subscriptionMatches, false);

        // Then
        verify(emailStrategy).send(userId1, "user1@example.com", matchedPosts);
        verify(emailStrategy).send(userId2, "user2@example.com", matchedPosts);
    }

    private Member createMember(Long id, String email, String name) {
        return Member.builder()
            .id(id)
            .email(email)
            .name(name)
            .password("password")
            .notificationEnabled(true)
            .isDeleted(false)
            .build();
    }

    private UserSubscription createSubscription(Long userId, Long skillId, Long jobFieldId, Long companyId) {
        return UserSubscription.of(userId, Set.of(skillId), Set.of(jobFieldId), Set.of(companyId));
    }

    private NotificationPreference createNotificationPreference(Long id, Long memberId, NotificationType type) {
        return NotificationPreference.builder()
            .id(id)
            .memberId(memberId)
            .notificationType(type)
            .enableInstant(false)
            .isDeleted(false)
            .build();
    }

    private NotificationPreference createNotificationPreference(Long id, Long memberId, NotificationType type, boolean enableInstant) {
        return NotificationPreference.builder()
            .id(id)
            .memberId(memberId)
            .notificationType(type)
            .enableInstant(enableInstant)
            .isDeleted(false)
            .build();
    }

    private MatchablePost createMatchablePost() {
        var post = Post.builder()
            .id(TEST_POST_ID)
            .companyId(TEST_COMPANY_ID)
            .jobRoleId(BACKEND_JOB_ROLE_ID)
            .build();

        var company = Company.builder()
            .id(TEST_COMPANY_ID)
            .name(TEST_COMPANY_NAME)
            .build();

        var jobRole = JobRole.builder()
            .id(BACKEND_JOB_ROLE_ID)
            .name(BACKEND_JOB_ROLE_NAME)
            .jobFieldId(BACKEND_JOB_FIELD_ID)
            .build();

        var postSkill = PostSkill.builder()
            .postId(TEST_POST_ID)
            .skillId(JAVA_SKILL_ID)
            .build();

        var skill = Skill.builder()
            .id(JAVA_SKILL_ID)
            .name(JAVA_SKILL_NAME)
            .build();

        var skills = List.of(new PostSkillWithSkill(postSkill, skill));

        return new MatchablePost(post, company, jobRole, skills);
    }
}
