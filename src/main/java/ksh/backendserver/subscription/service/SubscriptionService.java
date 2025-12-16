package ksh.backendserver.subscription.service;

import ksh.backendserver.company.entity.SubscriptionCompany;
import ksh.backendserver.company.repository.SubscriptionCompanyRepository;
import ksh.backendserver.group.entity.SubscriptionPosition;
import ksh.backendserver.group.repository.SubscriptionPositionRepository;
import ksh.backendserver.post.model.PostSkillRequirement;
import ksh.backendserver.skill.entity.SubscriptionSkill;
import ksh.backendserver.skill.repository.SubscriptionSkillRepository;
import ksh.backendserver.subscription.dto.request.SubscriptionCreationRequestDto;
import ksh.backendserver.subscription.model.UserSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SubscriptionPositionRepository subscriptionPositionRepository;
    private final SubscriptionCompanyRepository subscriptionCompanyRepository;

    @Transactional
    public void create(SubscriptionCreationRequestDto request) {
        List<SubscriptionCompany> companies = request.getCompanyIds().stream()
            .map(companyId -> SubscriptionCompany.builder()
                .userId(request.getMemberId())
                .companyId(companyId)
                .build())
            .toList();

        List<SubscriptionSkill> skills = request.getSkillIds().stream()
            .map(skillId -> SubscriptionSkill.builder()
                .userId(request.getMemberId())
                .skillId(skillId)
                .build())
            .toList();

        List<SubscriptionPosition> positions = request.getPositionIds().stream()
            .map(positionId -> SubscriptionPosition.builder()
                .userId(request.getMemberId())
                .positionId(positionId)
                .build())
            .toList();

        subscriptionCompanyRepository.saveAll(companies);
        subscriptionSkillRepository.saveAll(skills);
        subscriptionPositionRepository.saveAll(positions);
    }

    public Map<UserSubscription, List<PostSkillRequirement>> findMatchingSubscription(
        List<PostSkillRequirement> postings
    ) {
        List<SubscriptionSkill> allSubscribedSkills = subscriptionSkillRepository.findAll();
        List<SubscriptionPosition> allSubscribedPositions = subscriptionPositionRepository.findAll();
        List<SubscriptionCompany> allSubscribedCompanies = subscriptionCompanyRepository.findAll();

        Set<Long> userIds = collectAllSubscribingUserIds(
            allSubscribedSkills,
            allSubscribedPositions,
            allSubscribedCompanies
        );

        return buildPostMapByUsers(
            userIds,
            allSubscribedSkills,
            allSubscribedPositions,
            allSubscribedCompanies,
            postings
        );
    }

    private Set<Long> collectAllSubscribingUserIds(
        List<SubscriptionSkill> allSkills,
        List<SubscriptionPosition> allPositions,
        List<SubscriptionCompany> allCompanies
    ) {
        return Stream.of(
            allSkills.stream().map(SubscriptionSkill::getUserId),
            allPositions.stream().map(SubscriptionPosition::getUserId),
            allCompanies.stream().map(SubscriptionCompany::getUserId)
        ).flatMap(stream -> stream).collect(Collectors.toSet());
    }

    private Map<UserSubscription, List<PostSkillRequirement>> buildPostMapByUsers(
        Set<Long> userIds,
        List<SubscriptionSkill> allSubscribedSkills,
        List<SubscriptionPosition> allSubscribedPositions,
        List<SubscriptionCompany> allSubscribedCompanies,
        List<PostSkillRequirement> postings
    ) {
        Map<UserSubscription, List<PostSkillRequirement>> alertMap = new HashMap<>();

        for (Long userId : userIds) {
            UserSubscription userSubscription = buildUserSubscription(
                userId,
                allSubscribedSkills,
                allSubscribedPositions,
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
        List<SubscriptionPosition> allPositions,
        List<SubscriptionCompany> allCompanies
    ) {
        Set<Long> skillIds = extractIdsForUser(
            allSkills, SubscriptionSkill::getUserId, SubscriptionSkill::getSkillId, userId
        );
        Set<Long> positionIds = extractIdsForUser(
            allPositions, SubscriptionPosition::getUserId, SubscriptionPosition::getPositionId, userId
        );
        Set<Long> companyIds = extractIdsForUser(
            allCompanies, SubscriptionCompany::getUserId, SubscriptionCompany::getCompanyId, userId
        );

        return UserSubscription.of(userId, skillIds, positionIds, companyIds);
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
