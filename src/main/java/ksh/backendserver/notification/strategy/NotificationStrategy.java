package ksh.backendserver.notification.strategy;

import ksh.backendserver.notification.enums.NotificationType;
import ksh.backendserver.post.model.MatchablePost;

import java.util.List;

public interface NotificationStrategy {

    void send(Long memberId, String memberEmail, List<MatchablePost> matchedPosts);

    NotificationType getNotificationType();
}
