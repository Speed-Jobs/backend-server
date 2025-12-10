package ksh.backendserver.subscription.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class UserSubscription {

    private Long userId;
    private Set<Long> skillIds;
    private Set<Long> industryIds;
    private Set<Long> companyIds;

    public static UserSubscription of(Long userId, Set<Long> skillIds, Set<Long> industryIds, Set<Long> companyIds) {
        return new UserSubscription(userId, skillIds, industryIds, companyIds);
    }
}