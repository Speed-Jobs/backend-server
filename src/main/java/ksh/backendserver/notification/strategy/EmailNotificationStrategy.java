package ksh.backendserver.notification.strategy;

import ksh.backendserver.notification.enums.NotificationType;
import ksh.backendserver.notification.template.NotificationContentBuilder;
import ksh.backendserver.post.model.PostSkillRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationStrategy implements NotificationStrategy {

    private final JavaMailSender mailSender;
    private final NotificationContentBuilder notificationContentBuilder;

    @Value("${spring.mail.username}")
    private String senderAddress;

    @Override
    public void send(Long memberId, String memberEmail, List<PostSkillRequirement> matchedPosts) {
        if (matchedPosts == null || matchedPosts.isEmpty()) return;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderAddress);
        message.setTo(memberEmail);
        message.setSubject(notificationContentBuilder.buildSummary(matchedPosts));
        message.setText(notificationContentBuilder.buildBody(matchedPosts));

        int maxRetries = 3;
        int retryDelay = 2000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                mailSender.send(message);
                return;
            } catch (Exception e) {
                if (attempt == maxRetries) {
                    log.error("이메일 알림 전송 최종 실패. memberId={}, 재시도 횟수={}", memberId, maxRetries, e);
                } else {
                    try {
                        Thread.sleep(retryDelay * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("재시도 대기 중 인터럽트 발생. memberId={}", memberId);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.EMAIL;
    }
}
