package ksh.backendserver.subscription.model;

import ksh.backendserver.company.entity.SubscriptionCompany;
import ksh.backendserver.jobfield.entity.SubscriptionJobField;
import ksh.backendserver.skill.entity.SubscriptionSkill;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserSubscriptions {

    private final List<UserSubscription> subscriptions;

    private UserSubscriptions(List<UserSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public static UserSubscriptions from(
        List<SubscriptionSkill> skills,
        List<SubscriptionJobField> jobFields,
        List<SubscriptionCompany> companies
    ) {
        Map<Long, Set<Long>> skillIdsByUser = skills.stream()
            .collect(Collectors.groupingBy(
                SubscriptionSkill::getUserId,
                Collectors.mapping(SubscriptionSkill::getSkillId, Collectors.toSet())
            ));

        Map<Long, Set<Long>> jobFieldIdsByUser = jobFields.stream()
            .collect(Collectors.groupingBy(
                SubscriptionJobField::getUserId,
                Collectors.mapping(SubscriptionJobField::getJobFieldId, Collectors.toSet())
            ));

        Map<Long, Set<Long>> companyIdsByUser = companies.stream()
            .collect(Collectors.groupingBy(
                SubscriptionCompany::getUserId,
                Collectors.mapping(SubscriptionCompany::getCompanyId, Collectors.toSet())
            ));

        Set<Long> userIds = Stream.of(
            skillIdsByUser.keySet(),
            jobFieldIdsByUser.keySet(),
            companyIdsByUser.keySet()
        ).flatMap(Set::stream).collect(Collectors.toSet());

        List<UserSubscription> subscriptions = userIds.stream()
            .map(userId -> UserSubscription.of(
                userId,
                skillIdsByUser.getOrDefault(userId, Set.of()),
                jobFieldIdsByUser.getOrDefault(userId, Set.of()),
                companyIdsByUser.getOrDefault(userId, Set.of())
            ))
            .toList();

        return new UserSubscriptions(subscriptions);
    }

    public List<UserSubscription> getSubscriptions() {
        return subscriptions;
    }
}