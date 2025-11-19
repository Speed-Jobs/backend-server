package ksh.backendserver.skill.model;

import ksh.backendserver.skill.dto.projection.SkillWithCount;
import ksh.backendserver.skill.entity.Skill;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkillCloud {

    private long id;
    private String name;
    private long count;

    public static SkillCloud from(SkillWithCount projection) {
        Skill skill = projection.getSkill();

        return new SkillCloud(
            skill.getId(),
            skill.getName(),
            projection.getCount()
        );
    }
}
