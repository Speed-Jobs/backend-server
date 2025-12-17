package ksh.backendserver.subscription.service;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.company.entity.SubscriptionCompany;
import ksh.backendserver.company.repository.CompanyRepository;
import ksh.backendserver.company.repository.SubscriptionCompanyRepository;
import ksh.backendserver.group.entity.Position;
import ksh.backendserver.group.entity.SubscriptionPosition;
import ksh.backendserver.group.repository.PositionRepository;
import ksh.backendserver.group.repository.SubscriptionPositionRepository;
import ksh.backendserver.notification.entity.NotificationPreference;
import ksh.backendserver.notification.repository.NotificationPreferenceRepository;
import ksh.backendserver.post.model.PostSkillRequirement;
import ksh.backendserver.skill.entity.Skill;
import ksh.backendserver.skill.entity.SubscriptionSkill;
import ksh.backendserver.skill.repository.SkillRepository;
import ksh.backendserver.skill.repository.SubscriptionSkillRepository;
import ksh.backendserver.subscription.dto.request.SubscriptionCreationRequestDto;
import ksh.backendserver.subscription.dto.response.SubscriptionResponseDto;
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
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    private final CompanyRepository companyRepository;
    private final SkillRepository skillRepository;
    private final PositionRepository positionRepository;

    @Transactional
    public void save(SubscriptionCreationRequestDto request, Long memberId) {
        cancel(memberId);

        List<SubscriptionCompany> companies = request.getCompanyIds().stream()
            .map(companyId -> SubscriptionCompany.builder()
                .userId(memberId)
                .companyId(companyId)
                .build())
            .toList();

        List<SubscriptionSkill> skills = request.getSkillIds().stream()
            .map(skillId -> SubscriptionSkill.builder()
                .userId(memberId)
                .skillId(skillId)
                .build())
            .toList();

        List<SubscriptionPosition> positions = request.getPositionIds().stream()
            .map(positionId -> SubscriptionPosition.builder()
                .userId(memberId)
                .positionId(positionId)
                .build())
            .toList();

        List<NotificationPreference> notificationPreferences = request.getNotificationTypes()
            .stream()
            .map(notificationType -> NotificationPreference.builder()
                .memberId(memberId)
                .notificationType(notificationType)
                .build())
            .toList();

        subscriptionCompanyRepository.saveAll(companies);
        subscriptionSkillRepository.saveAll(skills);
        subscriptionPositionRepository.saveAll(positions);
        notificationPreferenceRepository.saveAll(notificationPreferences);
    }

    @Transactional
    public void cancel(Long memberId) {
        List<SubscriptionCompany> companies = subscriptionCompanyRepository.findByUserId(memberId);
        List<SubscriptionSkill> skills = subscriptionSkillRepository.findByUserId(memberId);
        List<SubscriptionPosition> positions = subscriptionPositionRepository.findByUserId(memberId);
        List<NotificationPreference> notificationPreferences = notificationPreferenceRepository.findByMemberId(memberId);

        subscriptionCompanyRepository.deleteAll(companies);
        subscriptionSkillRepository.deleteAll(skills);
        subscriptionPositionRepository.deleteAll(positions);
        notificationPreferenceRepository.deleteAll(notificationPreferences);
    }

    public SubscriptionResponseDto findByMemberId(Long memberId) {
        List<SubscriptionCompany> subscriptionCompanies = subscriptionCompanyRepository.findByUserId(memberId);
        List<SubscriptionSkill> subscriptionSkills = subscriptionSkillRepository.findByUserId(memberId);
        List<SubscriptionPosition> subscriptionPositions = subscriptionPositionRepository.findByUserId(memberId);
        List<NotificationPreference> notificationPreferences = notificationPreferenceRepository.findByMemberId(memberId);

        List<Long> companyIds = subscriptionCompanies.stream()
            .map(SubscriptionCompany::getCompanyId)
            .toList();

        List<Long> skillIds = subscriptionSkills.stream()
            .map(SubscriptionSkill::getSkillId)
            .toList();

        List<Long> positionIds = subscriptionPositions.stream()
            .map(SubscriptionPosition::getPositionId)
            .toList();

        List<String> companyNames = companyRepository.findAllById(companyIds).stream()
            .map(Company::getName)
            .toList();

        List<String> skillNames = skillRepository.findAllById(skillIds).stream()
            .map(Skill::getName)
            .toList();

        List<Integer> positionIntIds = positionIds.stream()
            .map(Long::intValue)
            .toList();

        List<String> positionNames = positionRepository.findAllById(positionIntIds).stream()
            .map(Position::getName)
            .toList();

        var notificationTypes = notificationPreferences.stream()
            .map(NotificationPreference::getNotificationType)
            .toList();

        return SubscriptionResponseDto.of(companyNames, skillNames, positionNames, notificationTypes);
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
