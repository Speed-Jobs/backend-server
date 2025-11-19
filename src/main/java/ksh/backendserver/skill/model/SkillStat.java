package ksh.backendserver.skill.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkillStat {

    private long countInPeriod;
    private double marketShare;
    private double weeklyChangeRate;
    private double monthlyChangeRate;

    public static SkillStat of(long countInPeriod, double marketShare, double weeklyChangeRate, double monthlyChangeRate) {
        return new SkillStat(countInPeriod, marketShare, weeklyChangeRate, monthlyChangeRate);
    }
}
