package ksh.backendserver.notification.facade;

import ksh.backendserver.notification.service.NotificationService;
import ksh.backendserver.post.service.MatchablePostService;
import ksh.backendserver.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationFacade {

    private final Clock clock;

    private final NotificationService notificationService;
    private final SubscriptionService subscriptionService;
    private final MatchablePostService matchablePostService;

    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyNotifications() {
        LocalDateTime checkpoint = LocalDateTime.now(clock).minusHours(25);

        var matchablePosts = matchablePostService.findNewMatchablePostsAfter(checkpoint);
        if (matchablePosts.isEmpty()) {
            return;
        }

        var subscriptionMatches = subscriptionService.findMatchingSubscription(matchablePosts);
        if (subscriptionMatches.isEmpty()) {
            return;
        }

        notificationService.sendNotifications(subscriptionMatches);
    }

    public void notifyNewPost(Long postId) {
        var matchablePost = matchablePostService.findMatchablePostById(postId);

        var subscriptionMatches = subscriptionService.findMatchingSubscription(List.of(matchablePost));
        if (subscriptionMatches.isEmpty()) {
            return;
        }

        notificationService.sendNotifications(subscriptionMatches, true);
    }
}
