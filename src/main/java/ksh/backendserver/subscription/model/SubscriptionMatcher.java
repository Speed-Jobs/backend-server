package ksh.backendserver.subscription.model;

import ksh.backendserver.post.model.MatchablePost;

import java.util.List;

public class SubscriptionMatcher {

    private SubscriptionMatcher() {
    }

    public static SubscriptionMatches match(UserSubscriptions subscriptions, List<MatchablePost> postings) {
        SubscriptionMatches matches = SubscriptionMatches.empty();

        for (UserSubscription subscription : subscriptions.getSubscriptions()) {
            List<MatchablePost> matchedPosts = findMatchedPosts(subscription, postings);
            matches.addMatch(subscription, matchedPosts);
        }

        return matches;
    }

    private static List<MatchablePost> findMatchedPosts(UserSubscription subscription, List<MatchablePost> postings) {
        return postings.stream()
            .filter(posting -> posting.matchesWith(subscription))
            .toList();
    }
}
