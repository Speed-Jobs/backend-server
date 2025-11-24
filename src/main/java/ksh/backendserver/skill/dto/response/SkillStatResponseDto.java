package ksh.backendserver.skill.dto.response;

import ksh.backendserver.skill.model.SkillStat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SkillStatResponseDto {

    private long count;
    private double marketShare;
    private double monthlyChangeRate;
    private boolean isMonthlyChangeRateAvailable;

    public static SkillStatResponseDto from(SkillStat skillStat) {
        if(skillStat == null) {
            return new SkillStatResponseDto();
        }

        return new SkillStatResponseDto(
            skillStat.getCountInPeriod(),
            skillStat.getMarketShare(),
            skillStat.getMonthlyChangeRate(),
            skillStat.isMonthlyChangeRateAvailable()
        );
    }
}
