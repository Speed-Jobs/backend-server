package ksh.backendserver.post.service;

import ksh.backendserver.company.enums.DateRange;
import ksh.backendserver.post.dto.projection.JobFieldCountProjection;
import ksh.backendserver.post.dto.projection.JobRoleCountProjection;
import ksh.backendserver.post.dto.request.JobFieldShareStatRequestDto;
import ksh.backendserver.post.dto.request.JobRoleShareStatRequestDto;
import ksh.backendserver.post.model.JobFieldShare;
import ksh.backendserver.post.model.JobRoleShare;
import ksh.backendserver.post.repository.PostRepository;
import ksh.backendserver.skill.model.SkillCloud;
import ksh.backendserver.skill.model.SkillCloudSnapshot;
import ksh.backendserver.skill.model.SkillStat;
import ksh.backendserver.skill.repository.PostSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostStatService {

    private final PostRepository postRepository;
    private final PostSkillRepository postSkillRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public SkillCloudSnapshot findSkillCloudsInitialSnapshot(int size, DateRange dateRange) {
        LocalDate today = LocalDate.now(clock);
        List<SkillCloud> topSkills = postSkillRepository
            .findTopSkillOrderByCountDesc(size, dateRange, today)
            .stream()
            .map(SkillCloud::from)
            .toList();

        if (topSkills.isEmpty()) {
            return SkillCloudSnapshot.empty();
        }

        SkillCloud topSkill = topSkills.getFirst();
        SkillStat topSkillStatistics = aggregateTopSkillStatistics(
            topSkill.getId(),
            dateRange
        );

        return SkillCloudSnapshot.of(topSkills, topSkillStatistics);
    }

    @Transactional(readOnly = true)
    public SkillStat getDetailStat(long id, DateRange dateRange) {
        return aggregateTopSkillStatistics(id, dateRange);
    }

    @Transactional(readOnly = true)
    public List<JobFieldShare> findPostDistributionByJobField(
        JobFieldShareStatRequestDto request
    ) {
        LocalDateTime now = LocalDateTime.now(clock);
        List<JobFieldCountProjection> projections =
            postRepository.countByFieldFilteredByFieldCategory(
                request,
                now
            );

        long totalPostCount = projections
            .stream()
            .mapToLong(JobFieldCountProjection::getPostCount)
            .sum();

        return projections
            .stream()
            .map(projection -> JobFieldShare.from(projection, totalPostCount))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<JobRoleShare> findPostDistributionByJobRoleOfField(
        JobRoleShareStatRequestDto request,
        long fieldId
    ) {
        LocalDateTime now = LocalDateTime.now(clock);
        List<JobRoleCountProjection> projections =
            postRepository.countByRoleFilteredByFieldId(
                request,
                fieldId,
                now
            );

        long totalPostCount = projections
            .stream()
            .mapToLong(JobRoleCountProjection::getPostCount)
            .sum();

        return projections
            .stream()
            .map(projection -> JobRoleShare.from(projection, totalPostCount))
            .toList();
    }

    private SkillStat aggregateTopSkillStatistics(long topSkillId, DateRange dateRange) {
        LocalDateTime periodStart = LocalDateTime.now(clock)
            .minusDays(dateRange.getDuration() - 1);

        long totalPostCountInPeriod = postRepository
            .countByPostedAtGreaterThanEqual(periodStart);
        long topSkillCountInPeriod = postSkillRepository
            .countBySkillIdSince(topSkillId, periodStart);
        double topSkillMarketShare = calculateMarketShare(
            topSkillCountInPeriod,
            totalPostCountInPeriod
        );

        boolean isMonthlyChangeRateAvailable = isPreviousPeriodDataAvailable(topSkillId, DateRange.MONTHLY.getDuration());
        double monthlyChangeRate = isMonthlyChangeRateAvailable
            ? calculatePeriodChangeRate(topSkillId, DateRange.MONTHLY.getDuration())
            : 0.0;

        return SkillStat.of(
            topSkillCountInPeriod,
            topSkillMarketShare,
            monthlyChangeRate,
            isMonthlyChangeRateAvailable
        );
    }

    private double calculateMarketShare(long skillCount, long totalPostCount) {
        if (totalPostCount == 0) {
            return 0.0;
        }
        double share = (double) skillCount / totalPostCount * 100;
        return Math.round(share * 10.0) / 10.0;
    }

    private double calculatePeriodChangeRate(long skillId, int rangeLength) {
        LocalDate today = LocalDate.now(clock);

        LocalDateTime previousPeriodStart = today
            .minusDays(rangeLength * 2L - 1L)
            .atStartOfDay();
        LocalDateTime previousPeriodEnd = today
            .minusDays(rangeLength - 1L)
            .atStartOfDay();
        LocalDateTime currentPeriodStart = today
            .minusDays(rangeLength - 1L)
            .atStartOfDay();
        LocalDateTime currentPeriodEnd = LocalDateTime.now(clock);

        long previousPeriodCount = postSkillRepository.countBySkillIdBetween(
            skillId,
            previousPeriodStart,
            previousPeriodEnd
        );
        long currentPeriodCount = postSkillRepository.countBySkillIdBetween(
            skillId,
            currentPeriodStart,
            currentPeriodEnd
        );

        return computeChangeRate(previousPeriodCount, currentPeriodCount);
    }

    private double computeChangeRate(long previousCount, long currentCount) {
        double changeRate = (double) (currentCount - previousCount) / previousCount * 100;
        return Math.round(changeRate * 10.0) / 10.0;
    }

    private boolean isPreviousPeriodDataAvailable(long skillId, int rangeLength) {
        LocalDate today = LocalDate.now(clock);

        LocalDateTime previousPeriodStart = today
            .minusDays(rangeLength * 2L - 1L)
            .atStartOfDay();
        LocalDateTime previousPeriodEnd = today
            .minusDays(rangeLength - 1L)
            .atStartOfDay();

        long previousPeriodCount = postSkillRepository.countBySkillIdBetween(
            skillId,
            previousPeriodStart,
            previousPeriodEnd
        );

        return previousPeriodCount > 0;
    }
}
