package ksh.backendserver.skill.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkillStat {

    private long countInPeriod;
    private double marketShare;
    private double monthlyChangeRate;
    private boolean isMonthlyChangeRateAvailable;

    public static SkillStat of(long countInPeriod, double marketShare, double monthlyChangeRate, boolean isMonthlyChangeRateAvailable) {
        return new SkillStat(countInPeriod, marketShare, monthlyChangeRate, isMonthlyChangeRateAvailable);
    }
}
