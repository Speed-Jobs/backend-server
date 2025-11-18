package ksh.backendserver.skill.dto.response;

import ksh.backendserver.skill.model.SkillStat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkillStatResponseDto {

    private long count;
    private double marketShare;
    private double weeklyChangeRate;
    private double monthlyChangeRate;

    public static SkillStatResponseDto from(SkillStat skillStat) {
        return new SkillStatResponseDto(
            skillStat.getCountInPeriod(),
            skillStat.getMarketShare(),
            skillStat.getWeeklyChangeRate(),
            skillStat.getMonthlyChangeRate()
        );
    }
}
