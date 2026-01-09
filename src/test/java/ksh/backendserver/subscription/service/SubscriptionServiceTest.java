package ksh.backendserver.subscription.service;

import ksh.backendserver.common.exception.CustomException;
import ksh.backendserver.common.exception.ErrorCode;
import ksh.backendserver.company.repository.SubscriptionCompanyRepository;
import ksh.backendserver.jobfield.repository.SubscriptionJobFieldRepository;
import ksh.backendserver.notification.enums.NotificationType;
import ksh.backendserver.notification.repository.NotificationPreferenceRepository;
import ksh.backendserver.skill.repository.SubscriptionSkillRepository;
import ksh.backendserver.subscription.dto.request.SubscriptionCreationRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionSkillRepository subscriptionSkillRepository;

    @Mock
    private SubscriptionJobFieldRepository subscriptionJobFieldRepository;

    @Mock
    private SubscriptionCompanyRepository subscriptionCompanyRepository;

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;

    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        subscriptionService = new SubscriptionService(
            subscriptionSkillRepository,
            subscriptionJobFieldRepository,
            subscriptionCompanyRepository,
            notificationPreferenceRepository
        );
    }

    @Test
    @DisplayName("즉시 알림 활성화 시 SLACK이 포함되어 있으면 검증을 통과한다")
    void validateInstantNotification_withSlack_success() {
        // Given
        var request = new SubscriptionCreationRequestDto(
            List.of(1L),
            List.of(1L),
            List.of(1L),
            List.of(NotificationType.SLACK),
            true
        );

        // When & Then
        assertThatCode(() -> subscriptionService.save(request, 1L))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("즉시 알림 활성화 시 SLACK이 없으면 예외가 발생한다")
    void validateInstantNotification_withoutSlack_throwsException() {
        // Given
        var request = new SubscriptionCreationRequestDto(
            List.of(1L),
            List.of(1L),
            List.of(1L),
            List.of(NotificationType.EMAIL),
            true
        );

        // When & Then
        assertThatThrownBy(() -> subscriptionService.save(request, 1L))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INSTANT_NOTIFICATION_NOT_ALLOWED);
    }

    @Test
    @DisplayName("즉시 알림 비활성화 시 SLACK이 없어도 검증을 통과한다")
    void validateInstantNotification_disabledWithoutSlack_success() {
        // Given
        var request = new SubscriptionCreationRequestDto(
            List.of(1L),
            List.of(1L),
            List.of(1L),
            List.of(NotificationType.EMAIL),
            false
        );

        // When & Then
        assertThatCode(() -> subscriptionService.save(request, 1L))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("즉시 알림 비활성화 시 SLACK이 있어도 검증을 통과한다")
    void validateInstantNotification_disabledWithSlack_success() {
        // Given
        var request = new SubscriptionCreationRequestDto(
            List.of(1L),
            List.of(1L),
            List.of(1L),
            List.of(NotificationType.EMAIL, NotificationType.SLACK),
            false
        );

        // When & Then
        assertThatCode(() -> subscriptionService.save(request, 1L))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("즉시 알림 활성화 시 EMAIL과 SLACK 모두 포함되어 있으면 검증을 통과한다")
    void validateInstantNotification_withEmailAndSlack_success() {
        // Given
        var request = new SubscriptionCreationRequestDto(
            List.of(1L),
            List.of(1L),
            List.of(1L),
            List.of(NotificationType.EMAIL, NotificationType.SLACK),
            true
        );

        // When & Then
        assertThatCode(() -> subscriptionService.save(request, 1L))
            .doesNotThrowAnyException();
    }
}
