package ksh.backendserver.skill.controller;

import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.skill.dto.response.SkillResponseDto;
import ksh.backendserver.skill.dto.response.SkillResponseListDto;
import ksh.backendserver.skill.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

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
