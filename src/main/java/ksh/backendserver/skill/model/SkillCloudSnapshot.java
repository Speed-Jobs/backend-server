package ksh.backendserver.skill.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SkillCloudSnapshot {

    private List<SkillCloud> topSkills;
    private SkillStat topSkillStatistics;

    public static SkillCloudSnapshot of(List<SkillCloud> topSkills, SkillStat topSkillStatistics) {
        return new SkillCloudSnapshot(topSkills, topSkillStatistics);
    }
}
