package ksh.backendserver.skill.dto.projection;

import ksh.backendserver.skill.entity.Skill;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkillWithCount {

    private Skill skill;
    private int count;
}
