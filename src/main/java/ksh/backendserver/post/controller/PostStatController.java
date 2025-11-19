package ksh.backendserver.post.controller;

import jakarta.validation.Valid;
import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.company.enums.DateRange;
import ksh.backendserver.group.enums.GroupCategory;
import ksh.backendserver.post.dto.request.GroupShareStatRequestDto;
import ksh.backendserver.post.dto.response.GroupShareResponseDto;
import ksh.backendserver.post.dto.response.GroupSharesResponseDto;
import ksh.backendserver.post.service.PostStatService;
import ksh.backendserver.skill.dto.request.SkillStatRequestDto;
import ksh.backendserver.skill.dto.response.SkillCloudSnapshotResponseDto;
import ksh.backendserver.skill.dto.response.SkillStatResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostStatController {

    private final PostStatService postStatService;

    @GetMapping("/api/v1/dashboard/skills/init")
    public ApiResponseDto<SkillCloudSnapshotResponseDto> initialSkillStat(
        @Valid SkillStatRequestDto request
    ) {
        var snapshot = postStatService.findSkillCloudsInitialSnapshot(
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

    @GetMapping("/api/v1/dashboard/skills/{id}")
    public ApiResponseDto<SkillStatResponseDto> skillStat(
        @PathVariable("id") long id,
        @RequestParam(defaultValue = "MONTHLY") DateRange dateRange
    ) {
        var stat = postStatService.getDetailStat(id, dateRange);

        var body = SkillStatResponseDto.from(stat);

        return ApiResponseDto.of(
            HttpStatus.OK.value(),
            HttpStatus.OK.name(),
            "특정 스킬의 상세 통계 조회 성공",
            body
        );
    }

    //TODO: RequestParam 기본값 잘 동작하는지 테스트
    @GetMapping("/api/v1/dashboard/job-group")
    public ApiResponseDto<GroupSharesResponseDto> distributionByJobGroup(
        @Valid GroupShareStatRequestDto request
        ) {
        var distributions = postStatService.findJobGroupDistribution(request)
            .stream()
            .map(GroupShareResponseDto::from)
            .toList();

        var body = GroupSharesResponseDto.from(distributions);

        return ApiResponseDto.of(
            HttpStatus.OK.value(),
            HttpStatus.OK.name(),
            "직군 별 공고 분포 조회 성공",
            body
        );

    }
}
