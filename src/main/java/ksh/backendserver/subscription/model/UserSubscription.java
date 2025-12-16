package ksh.backendserver.subscription.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class UserSubscription {

    private Long userId;
    private Set<Long> skillIds;
    private Set<Long> positionIds;
    private Set<Long> companyIds;

    public static UserSubscription of(Long userId, Set<Long> skillIds, Set<Long> positionIds, Set<Long> companyIds) {
        return new UserSubscription(userId, skillIds, positionIds, companyIds);
    }
}