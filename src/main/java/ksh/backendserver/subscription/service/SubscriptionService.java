package ksh.backendserver.subscription.service;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.company.entity.SubscriptionCompany;
import ksh.backendserver.company.repository.CompanyRepository;
import ksh.backendserver.company.repository.SubscriptionCompanyRepository;
import ksh.backendserver.jobfield.entity.JobField;
import ksh.backendserver.jobfield.entity.SubscriptionJobField;
import ksh.backendserver.jobfield.repository.JobFieldRepository;
import ksh.backendserver.jobfield.repository.SubscriptionJobFieldRepository;
import ksh.backendserver.notification.entity.NotificationPreference;
import ksh.backendserver.notification.enums.NotificationType;
import ksh.backendserver.notification.repository.NotificationPreferenceRepository;
import ksh.backendserver.post.model.MatchablePost;
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
    private final SubscriptionJobFieldRepository subscriptionJobFieldRepository;
    private final SubscriptionCompanyRepository subscriptionCompanyRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    private final CompanyRepository companyRepository;
    private final SkillRepository skillRepository;
    private final JobFieldRepository jobFieldRepository;

    @Transactional
    public void save(SubscriptionCreationRequestDto request, Long memberId) {
        cancel(memberId);

        saveSubscriptionCompanies(request.getCompanyIds(), memberId);
        saveSubscriptionSkills(request.getSkillIds(), memberId);
        saveSubscriptionJobFields(request.getJobFieldIds(), memberId);
        saveNotificationPreferences(request.getNotificationTypes(), memberId);
    }

    @Transactional
    public void cancel(Long memberId) {
        subscriptionCompanyRepository.deleteByUserId(memberId);
        subscriptionSkillRepository.deleteByUserId(memberId);
        subscriptionJobFieldRepository.deleteByUserId(memberId);
        notificationPreferenceRepository.deleteByMemberId(memberId);
    }

    public SubscriptionResponseDto findByMemberId(Long memberId) {
        List<SubscriptionCompany> subscriptionCompanies = subscriptionCompanyRepository.findByUserId(memberId);
        List<SubscriptionSkill> subscriptionSkills = subscriptionSkillRepository.findByUserId(memberId);
        List<SubscriptionJobField> subscriptionJobFields = subscriptionJobFieldRepository.findByUserId(memberId);
        List<NotificationPreference> notificationPreferences = notificationPreferenceRepository.findByMemberId(memberId);

        List<Long> companyIds = subscriptionCompanies.stream()
            .map(SubscriptionCompany::getCompanyId)
            .toList();

        List<Long> skillIds = subscriptionSkills.stream()
            .map(SubscriptionSkill::getSkillId)
            .toList();

        List<Long> jobFieldIds = subscriptionJobFields.stream()
            .map(SubscriptionJobField::getJobFieldId)
            .toList();

        List<String> companyNames = companyRepository.findAllById(companyIds).stream()
            .map(Company::getName)
            .toList();

        List<String> skillNames = skillRepository.findAllById(skillIds).stream()
            .map(Skill::getName)
            .toList();

        List<String> jobFieldNames = jobFieldRepository.findAllById(jobFieldIds).stream()
            .map(JobField::getName)
            .toList();

        var notificationTypes = notificationPreferences.stream()
            .map(NotificationPreference::getNotificationType)
            .toList();

        return SubscriptionResponseDto.of(companyNames, skillNames, jobFieldNames, notificationTypes);
    }

    public Map<UserSubscription, List<MatchablePost>> findMatchingSubscription(
        List<MatchablePost> postings
    ) {
        List<SubscriptionSkill> allSubscribedSkills = subscriptionSkillRepository.findAll();
        List<SubscriptionJobField> allSubscribedJobFields = subscriptionJobFieldRepository.findAll();
        List<SubscriptionCompany> allSubscribedCompanies = subscriptionCompanyRepository.findAll();

        Set<Long> userIds = collectAllSubscribingUserIds(
            allSubscribedSkills,
            allSubscribedJobFields,
            allSubscribedCompanies
        );

        return buildPostMapByUsers(
            userIds,
            allSubscribedSkills,
            allSubscribedJobFields,
            allSubscribedCompanies,
            postings
        );
    }

    private void saveSubscriptionCompanies(List<Long> companyIds, Long memberId) {
        List<SubscriptionCompany> companies = companyIds.stream()
            .map(companyId -> SubscriptionCompany.builder()
                .userId(memberId)
                .companyId(companyId)
                .build())
            .toList();
        subscriptionCompanyRepository.saveAll(companies);
    }

    private void saveSubscriptionSkills(List<Long> skillIds, Long memberId) {
        List<SubscriptionSkill> skills = skillIds.stream()
            .map(skillId -> SubscriptionSkill.builder()
                .userId(memberId)
                .skillId(skillId)
                .build())
            .toList();
        subscriptionSkillRepository.saveAll(skills);
    }

    private void saveSubscriptionJobFields(List<Long> jobFieldIds, Long memberId) {
        List<SubscriptionJobField> jobFields = jobFieldIds.stream()
            .map(jobFieldId -> SubscriptionJobField.builder()
                .userId(memberId)
                .jobFieldId(jobFieldId)
                .build())
            .toList();
        subscriptionJobFieldRepository.saveAll(jobFields);
    }

    private void saveNotificationPreferences(List<NotificationType> notificationTypes, Long memberId) {
        List<NotificationPreference> notificationPreferences = notificationTypes.stream()
            .map(notificationType -> NotificationPreference.builder()
                .memberId(memberId)
                .notificationType(notificationType)
                .build())
            .toList();
        notificationPreferenceRepository.saveAll(notificationPreferences);
    }

    private Set<Long> collectAllSubscribingUserIds(
        List<SubscriptionSkill> allSkills,
        List<SubscriptionJobField> allJobFields,
        List<SubscriptionCompany> allCompanies
    ) {
        return Stream.of(
            allSkills.stream().map(SubscriptionSkill::getUserId),
            allJobFields.stream().map(SubscriptionJobField::getUserId),
            allCompanies.stream().map(SubscriptionCompany::getUserId)
        ).flatMap(stream -> stream).collect(Collectors.toSet());
    }

    private Map<UserSubscription, List<MatchablePost>> buildPostMapByUsers(
        Set<Long> userIds,
        List<SubscriptionSkill> allSubscribedSkills,
        List<SubscriptionJobField> allSubscribedJobFields,
        List<SubscriptionCompany> allSubscribedCompanies,
        List<MatchablePost> postings
    ) {
        Map<UserSubscription, List<MatchablePost>> alertMap = new HashMap<>();

        for (Long userId : userIds) {
            UserSubscription userSubscription = buildUserSubscription(
                userId,
                allSubscribedSkills,
                allSubscribedJobFields,
                allSubscribedCompanies
            );

            List<MatchablePost> matchedPosts = findMatchingPosts(
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
        List<SubscriptionJobField> allJobFields,
        List<SubscriptionCompany> allCompanies
    ) {
        Set<Long> skillIds = extractIdsForUser(
            allSkills, SubscriptionSkill::getUserId, SubscriptionSkill::getSkillId, userId
        );
        Set<Long> jobFieldIds = extractIdsForUser(
            allJobFields, SubscriptionJobField::getUserId, SubscriptionJobField::getJobFieldId, userId
        );
        Set<Long> companyIds = extractIdsForUser(
            allCompanies, SubscriptionCompany::getUserId, SubscriptionCompany::getCompanyId, userId
        );

        return UserSubscription.of(userId, skillIds, jobFieldIds, companyIds);
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

    private List<MatchablePost> findMatchingPosts(
        UserSubscription userSubscription,
        List<MatchablePost> postings
    ) {
        return postings.stream()
            .filter(posting -> posting.matchesWith(userSubscription))
            .toList();
    }
}
