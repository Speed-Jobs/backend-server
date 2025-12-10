package ksh.backendserver.subscription.service;

import ksh.backendserver.company.entity.SubscriptionCompany;
import ksh.backendserver.company.repository.SubscriptionCompanyRepository;
import ksh.backendserver.industry.entity.SubscriptionIndustry;
import ksh.backendserver.industry.repository.SubscriptionIndustryRepository;
import ksh.backendserver.post.model.PostSkillRequirement;
import ksh.backendserver.skill.entity.SubscriptionSkill;
import ksh.backendserver.skill.repository.SubscriptionSkillRepository;
import ksh.backendserver.subscription.model.UserSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionSkillRepository subscriptionSkillRepository;
    private final SubscriptionIndustryRepository subscriptionIndustryRepository;
    private final SubscriptionCompanyRepository subscriptionCompanyRepository;

    public Map<UserSubscription, List<PostSkillRequirement>> findMatchingAlerts(
        List<PostSkillRequirement> postings
    ) {
        List<SubscriptionSkill> allSubscribedSkills = subscriptionSkillRepository.findAll();
        List<SubscriptionIndustry> allSubscribedIndustries = subscriptionIndustryRepository.findAll();
        List<SubscriptionCompany> allSubscribedCompanies = subscriptionCompanyRepository.findAll();

        Set<Long> userIds = collectAllSubscribingUserIds(
            allSubscribedSkills,
            allSubscribedIndustries,
            allSubscribedCompanies
        );

        return buildPostMapByUsers(
            userIds,
            allSubscribedSkills,
            allSubscribedIndustries,
            allSubscribedCompanies,
            postings
        );
    }

    private Set<Long> collectAllSubscribingUserIds(
        List<SubscriptionSkill> allSkills,
        List<SubscriptionIndustry> allIndustries,
        List<SubscriptionCompany> allCompanies
    ) {
        return Stream.of(
            allSkills.stream().map(SubscriptionSkill::getUserId),
            allIndustries.stream().map(SubscriptionIndustry::getUserId),
            allCompanies.stream().map(SubscriptionCompany::getUserId)
        ).flatMap(stream -> stream).collect(Collectors.toSet());
    }

    private Map<UserSubscription, List<PostSkillRequirement>> buildPostMapByUsers(
        Set<Long> userIds,
        List<SubscriptionSkill> allSubscribedSkills,
        List<SubscriptionIndustry> allSubscribedIndustries,
        List<SubscriptionCompany> allSubscribedCompanies,
        List<PostSkillRequirement> postings
    ) {
        Map<UserSubscription, List<PostSkillRequirement>> alertMap = new HashMap<>();

        for (Long userId : userIds) {
            UserSubscription userSubscription = buildUserSubscription(
                userId,
                allSubscribedSkills,
                allSubscribedIndustries,
                allSubscribedCompanies
            );

            List<PostSkillRequirement> matchedPosts = findMatchingPosts(
                userSubscription,
                postings
            );

            if (!matchedPosts.isEmpty()) {
                alertMap.put(userSubscription, matchedPosts);
            }
        }

        return alertMap;
    }

    private UserSubscription buildUserSubscription(
        Long userId,
        List<SubscriptionSkill> allSkills,
        List<SubscriptionIndustry> allIndustries,
        List<SubscriptionCompany> allCompanies
    ) {
        Set<Long> skillIds = extractIdsForUser(
            allSkills, SubscriptionSkill::getUserId, SubscriptionSkill::getSkillId, userId
        );
        Set<Long> industryIds = extractIdsForUser(
            allIndustries, SubscriptionIndustry::getUserId, SubscriptionIndustry::getIndustryId, userId
        );
        Set<Long> companyIds = extractIdsForUser(
            allCompanies, SubscriptionCompany::getUserId, SubscriptionCompany::getCompanyId, userId
        );

        return UserSubscription.of(userId, skillIds, industryIds, companyIds);
    }

    private <T> Set<Long> extractIdsForUser(
        List<T> subscriptions,
        Function<T, Long> userIdExtractor,
        Function<T, Long> idExtractor,
        Long targetUserId
    ) {
        return subscriptions.stream()
            .filter(subscription -> userIdExtractor.apply(subscription).equals(targetUserId))
            .map(idExtractor)
            .collect(Collectors.toSet());
    }

    private List<PostSkillRequirement> findMatchingPosts(
        UserSubscription userSubscription,
        List<PostSkillRequirement> postings
    ) {
        return postings.stream()
            .filter(posting -> posting.matchesWith(userSubscription))
            .toList();
    }
}
