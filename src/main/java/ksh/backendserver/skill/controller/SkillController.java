package ksh.backendserver.skill.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.skill.dto.response.SkillResponseDto;
import ksh.backendserver.skill.dto.response.SkillResponseListDto;
import ksh.backendserver.skill.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "기술 스택", description = "기술 스택 조회 API")
@RestController
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @Operation(
        summary = "주요 기술 스택 목록 조회",
        description = "주요 기술 스택 목록을 조회합니다. 주로 필터링 옵션으로 사용됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/api/v1/skills/major")
    public ApiResponseDto<SkillResponseListDto> majorSkills() {
        var dtos = skillService.findMajorSkills()
            .stream()
            .map(SkillResponseDto::from)
            .toList();

        var body = SkillResponseListDto.of(dtos);

        return ApiResponseDto.of(
            HttpStatus.OK.value(),
            HttpStatus.OK.name(),
            "주요 기술 조회 성공",
            body
        );
    }
}
