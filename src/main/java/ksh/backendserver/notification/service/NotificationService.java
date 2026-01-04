package ksh.backendserver.notification.service;

import ksh.backendserver.common.exception.CustomException;
import ksh.backendserver.common.exception.ErrorCode;
import ksh.backendserver.member.entity.Member;
import ksh.backendserver.member.repository.MemberRepository;
import ksh.backendserver.notification.entity.NotificationPreference;
import ksh.backendserver.notification.enums.NotificationType;
import ksh.backendserver.notification.repository.NotificationPreferenceRepository;
import ksh.backendserver.notification.strategy.NotificationStrategy;
import ksh.backendserver.post.model.MatchablePost;
import ksh.backendserver.subscription.model.UserSubscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationService {

    private final Map<NotificationType, NotificationStrategy> strategyMap;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final MemberRepository memberRepository;

    public NotificationService(
        List<NotificationStrategy> strategies,
        NotificationPreferenceRepository notificationPreferenceRepository,
        MemberRepository memberRepository
    ) {
        this.strategyMap = strategies.stream()
            .collect(Collectors.toMap(
                NotificationStrategy::getNotificationType,
                Function.identity()
            ));
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.memberRepository = memberRepository;
    }

    public void sendNotifications(Map<UserSubscription, List<MatchablePost>> alertMap) {
        alertMap.forEach((subscription, matchedPosts) ->
            sendNotificationsToUser(subscription.getUserId(), matchedPosts)
        );
    }

    private void sendNotificationsToUser(Long memberId, List<MatchablePost> matchedPosts) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND, List.of(memberId)));

        List<NotificationPreference> preferences = notificationPreferenceRepository.findByMemberId(memberId);

        preferences.forEach(preference ->
            sendNotificationByType(member, preference.getNotificationType(), matchedPosts)
        );
    }

    private void sendNotificationByType(
        Member member,
        NotificationType type,
        List<MatchablePost> matchedPosts
    ) {
        NotificationStrategy notificationStrategy = strategyMap.get(type);

        try {
            notificationStrategy.send(member.getId(), member.getEmail(), matchedPosts);
        } catch (Exception e) {
            log.error("memberId={}에게 {} 알림 전송 실패", member.getId(), type, e);
        }
    }
}
