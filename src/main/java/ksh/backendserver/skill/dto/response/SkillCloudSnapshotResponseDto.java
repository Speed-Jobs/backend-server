package ksh.backendserver.skill.dto.response;

import ksh.backendserver.skill.model.SkillCloudSnapshot;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SkillCloudSnapshotResponseDto {

    private List<SkillCloudResponseDto> topSkills;
    private SkillStatResponseDto topSkillStat;

    public static SkillCloudSnapshotResponseDto from(SkillCloudSnapshot snapshot) {
        var topSkills = snapshot.getTopSkills()
            .stream()
            .map(SkillCloudResponseDto::from)
            .toList();

        var topSkillStat = SkillStatResponseDto.from(snapshot.getTopSkillStatistics());

        return new SkillCloudSnapshotResponseDto(topSkills, topSkillStat);
    }
}
