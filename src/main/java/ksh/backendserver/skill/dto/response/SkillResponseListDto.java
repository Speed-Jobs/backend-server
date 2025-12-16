package ksh.backendserver.skill.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SkillResponseListDto {

    public List<SkillResponseDto> skills;

    public static SkillResponseListDto of(List<SkillResponseDto> skills) {
        return new SkillResponseListDto(skills);
    }
}
