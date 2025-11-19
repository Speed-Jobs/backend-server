package ksh.backendserver.skill.dto.response;

import ksh.backendserver.skill.model.SkillCloud;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkillCloudResponseDto {

    private long id;
    private String name;

    public static SkillCloudResponseDto from(SkillCloud cloud) {
        return new SkillCloudResponseDto(cloud.getId(), cloud.getName());
    }
}
