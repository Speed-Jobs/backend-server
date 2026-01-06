package ksh.backendserver.subscription.model;

import ksh.backendserver.post.model.MatchablePost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class SubscriptionMatches {

    private final Map<UserSubscription, List<MatchablePost>> matches;

    private SubscriptionMatches(Map<UserSubscription, List<MatchablePost>> matches) {
        this.matches = matches;
    }

    public static SubscriptionMatches empty() {
        return new SubscriptionMatches(new HashMap<>());
    }

    public static SubscriptionMatches of(Map<UserSubscription, List<MatchablePost>> matches) {
        return new SubscriptionMatches(new HashMap<>(matches));
    }

    public void addMatch(UserSubscription subscription, List<MatchablePost> posts) {
        if (!posts.isEmpty()) {
            matches.put(subscription, posts);
        }
    }

    public boolean isEmpty() {
        return matches.isEmpty();
    }

    public void forEachMatch(BiConsumer<UserSubscription, List<MatchablePost>> action) {
        matches.forEach(action);
    }

    public Map<UserSubscription, List<MatchablePost>> getMatches() {
        return new HashMap<>(matches);
    }
}
