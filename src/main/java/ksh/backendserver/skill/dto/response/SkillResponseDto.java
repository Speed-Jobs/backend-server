package ksh.backendserver.skill.dto.response;

import ksh.backendserver.skill.entity.Skill;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkillResponseDto {

    private long id;
    private String name;

    public static SkillResponseDto from(Skill skill) {
        return new SkillResponseDto(
            skill.getId(),
            skill.getName()
        );
    }
}
