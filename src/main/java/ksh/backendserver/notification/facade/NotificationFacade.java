package ksh.backendserver.notification.facade;

import ksh.backendserver.notification.service.NotificationService;
import ksh.backendserver.post.service.PostService;
import ksh.backendserver.skill.service.PostSkillService;
import ksh.backendserver.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationFacade {

    private final Clock clock;

    private final NotificationService notificationService;
    private final SubscriptionService subscriptionService;
    private final PostService postService;
    private final PostSkillService postSkillService;

    @Scheduled(cron = "0 0 8 * * *")
    public void sendNotifications() {
        LocalDateTime checkpoint = LocalDateTime.now(clock).minusHours(25);

        var newPosts = postService.findNewPostsAfter(checkpoint);
        if (newPosts.isEmpty()) {
            return;
        }

        var postSkillRequirements = postSkillService.findSkillRequirementsOf(newPosts);

        var matchedSubscriptionsMap = subscriptionService.findMatchingSubscription(postSkillRequirements);
        if (matchedSubscriptionsMap.isEmpty()) {
            return;
        }

        notificationService.sendNotifications(matchedSubscriptionsMap);
    }
}
