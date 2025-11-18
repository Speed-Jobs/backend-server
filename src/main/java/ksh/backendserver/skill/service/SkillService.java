package ksh.backendserver.skill.service;

import ksh.backendserver.company.enums.DateRange;
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
public class SkillService {

    private static final double CHANGE_RATE_UNAVAILABLE = -1.0;

    private final PostRepository postRepository;
    private final PostSkillRepository postSkillRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public SkillCloudSnapshot findSkillCloudsInitialSnapshot(int size, DateRange timePeriod) {
        LocalDate today = LocalDate.now(clock);
        List<SkillCloud> topSkills = postSkillRepository
            .findTopSkillOrderByCountDesc(size, timePeriod, today)
            .stream()
            .map(SkillCloud::from)
            .toList();

        SkillCloud topSkill = topSkills.getFirst();
        SkillStat topSkillStatistics = aggregateTopSkillStatistics(
            topSkill.getId(),
            timePeriod
        );

        return SkillCloudSnapshot.of(topSkills, topSkillStatistics);
    }

    private SkillStat aggregateTopSkillStatistics(long topSkillId, DateRange timePeriod) {
        LocalDateTime periodStart = LocalDateTime.now(clock)
            .minusDays(timePeriod.getDuration() - 1);

        long totalPostCountInPeriod = postRepository
            .countByPostedAtGreaterThanEqual(periodStart);
        long topSkillCountInPeriod = postSkillRepository
            .countBySkillIdSince(topSkillId, periodStart);
        double topSkillMarketShare = calculateMarketShare(
            topSkillCountInPeriod,
            totalPostCountInPeriod
        );

        double weeklyChangeRate = calculatePeriodChangeRate(
            topSkillId,
            DateRange.WEEKLY.getDuration()
        );
        double monthlyChangeRate = calculatePeriodChangeRate(
            topSkillId,
            DateRange.MONTHLY.getDuration()
        );

        return SkillStat.of(
            topSkillCountInPeriod,
            topSkillMarketShare,
            weeklyChangeRate,
            monthlyChangeRate
        );
    }

    private double calculateMarketShare(long skillCount, long totalPostCount) {
        if (totalPostCount == 0) {
            return 0.0;
        }
        double share = (double) skillCount / totalPostCount * 100;
        return Math.round(share * 10.0) / 10.0;
    }

    private double calculatePeriodChangeRate(long skillId, int periodDays) {
        LocalDate today = LocalDate.now(clock);

        LocalDateTime previousPeriodStart = today
            .minusDays(periodDays * 2L - 1L)
            .atStartOfDay();
        LocalDateTime previousPeriodEnd = today
            .minusDays(periodDays - 1L)
            .atStartOfDay();
        LocalDateTime currentPeriodStart = today
            .minusDays(periodDays - 1L)
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
        if (previousCount == 0) {
            return CHANGE_RATE_UNAVAILABLE;
        }
        double changeRate = (double) (currentCount - previousCount) / previousCount * 100;
        return Math.round(changeRate * 10.0) / 10.0;
    }
}
