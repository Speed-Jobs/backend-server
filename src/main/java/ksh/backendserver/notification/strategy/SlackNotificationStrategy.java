package ksh.backendserver.notification.strategy;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.response.users.UsersLookupByEmailResponse;
import ksh.backendserver.notification.enums.NotificationType;
import ksh.backendserver.notification.template.NotificationContentBuilder;
import ksh.backendserver.post.model.PostSkillRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackNotificationStrategy implements NotificationStrategy {

    @Value("${slack.bot-token}")
    private String botToken;

    private final Slack slack;
    private final NotificationContentBuilder contentBuilder;

    @Override
    public void send(
        Long memberId,
        String memberEmail,
        List<PostSkillRequirement> matchedPosts
    ) {
        try {
            MethodsClient methods = slack.methods(botToken);

            UsersLookupByEmailResponse userResponse = methods
                .usersLookupByEmail(req -> req.email(memberEmail));

            if (!userResponse.isOk()) {
                log.error("Slack 유저 조회 실패: email={}, error={}", memberEmail, userResponse.getError());
                return;
            }

            String userSlackId = userResponse.getUser().getId();
            String message = contentBuilder.buildBody(matchedPosts);
            methods.chatPostMessage(req -> req
                .channel(userSlackId)
                .text(message)
            );
        } catch (Exception e) {
            log.error("Slack API 호출 실패: email={}", memberEmail, e);
        }
    }

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SLACK;
    }
}
