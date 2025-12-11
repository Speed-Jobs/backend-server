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

        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("이메일 알림 전송 실패. memberId={}", memberId, e);
        }
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.EMAIL;
    }
}
