package ksh.backendserver.subscription.service;

import ksh.backendserver.company.entity.SubscriptionCompany;
import ksh.backendserver.company.repository.SubscriptionCompanyRepository;
import ksh.backendserver.jobfield.entity.SubscriptionJobField;
import ksh.backendserver.jobfield.repository.SubscriptionJobFieldRepository;
import ksh.backendserver.notification.entity.NotificationPreference;
import ksh.backendserver.notification.enums.NotificationType;
import ksh.backendserver.notification.repository.NotificationPreferenceRepository;
import ksh.backendserver.post.model.MatchablePost;
import ksh.backendserver.skill.entity.SubscriptionSkill;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionSkillRepository subscriptionSkillRepository;
    private final SubscriptionJobFieldRepository subscriptionJobFieldRepository;
    private final SubscriptionCompanyRepository subscriptionCompanyRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;

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
        List<String> companyNames = subscriptionCompanyRepository.findCompanyNamesByUserId(memberId);
        List<String> skillNames = subscriptionSkillRepository.findSkillNamesByUserId(memberId);
        List<String> jobFieldNames = subscriptionJobFieldRepository.findJobFieldNamesByUserId(memberId);

        var notificationTypes = notificationPreferenceRepository.findByMemberId(memberId).stream()
            .map(NotificationPreference::getNotificationType)
            .toList();

        return SubscriptionResponseDto.of(companyNames, skillNames, jobFieldNames, notificationTypes);
    }

    public Map<UserSubscription, List<MatchablePost>> findMatchingSubscription(
        List<MatchablePost> postings
    ) {
        Map<Long, Set<Long>> skillIdsByUser = subscriptionSkillRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                SubscriptionSkill::getUserId,
                Collectors.mapping(SubscriptionSkill::getSkillId, Collectors.toSet())
            ));

        Map<Long, Set<Long>> jobFieldIdsByUser = subscriptionJobFieldRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                SubscriptionJobField::getUserId,
                Collectors.mapping(SubscriptionJobField::getJobFieldId, Collectors.toSet())
            ));

        Map<Long, Set<Long>> companyIdsByUser = subscriptionCompanyRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                SubscriptionCompany::getUserId,
                Collectors.mapping(SubscriptionCompany::getCompanyId, Collectors.toSet())
            ));

        Set<Long> userIds = Stream.of(
            skillIdsByUser.keySet(),
            jobFieldIdsByUser.keySet(),
            companyIdsByUser.keySet()
        ).flatMap(Set::stream).collect(Collectors.toSet());

        Map<UserSubscription, List<MatchablePost>> result = new HashMap<>();

        for (Long userId : userIds) {
            UserSubscription userSubscription = UserSubscription.of(
                userId,
                skillIdsByUser.getOrDefault(userId, Set.of()),
                jobFieldIdsByUser.getOrDefault(userId, Set.of()),
                companyIdsByUser.getOrDefault(userId, Set.of())
            );

            List<MatchablePost> matchedPosts = postings.stream()
                .filter(posting -> posting.matchesWith(userSubscription))
                .toList();

            if (!matchedPosts.isEmpty()) {
                result.put(userSubscription, matchedPosts);
            }
        }

        return result;
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
}
