package ksh.backendserver.skill.dto.projection;

import ksh.backendserver.skill.entity.PostSkill;
import ksh.backendserver.skill.entity.Skill;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostSkillWithSkill {

    private PostSkill postSkill;
    private Skill skill;
}
