package ksh.backendserver.skill.controller;

import jakarta.validation.Valid;
import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.skill.dto.request.SkillStatRequestDto;
import ksh.backendserver.skill.dto.response.SkillCloudSnapshotResponseDto;
import ksh.backendserver.skill.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping("/dashboard/skills/init")
    public ApiResponseDto<SkillCloudSnapshotResponseDto> initialSkillStat(
        @Valid SkillStatRequestDto request
    ) {
        var snapshot = skillService.findSkillCloudsInitialSnapshot(
            request.getSize(),
            request.getDateRange()
        );

        var body = SkillCloudSnapshotResponseDto.from(snapshot);

        return ApiResponseDto.of(
            HttpStatus.OK.value(),
            HttpStatus.OK.name(),
            "상위 스킬 목록과 순위가 제일 높은 스킬의 상세 통계 조회 성공",
            body
        );
    }
}
