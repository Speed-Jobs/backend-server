package ksh.backendserver.jobfield.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.jobfield.dto.response.JobFieldOptionListResponseDto;
import ksh.backendserver.jobfield.dto.response.JobFieldOptionResponseDto;
import ksh.backendserver.jobfield.service.JobFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "직군", description = "직군 조회 API")
@RestController
@RequiredArgsConstructor
public class JobFieldController {

    private final JobFieldService jobFieldService;

    @Operation(
        summary = "전체 직군 목록 조회",
        description = "전체 직군 목록을 조회합니다. 주로 필터링 옵션으로 사용됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/api/v1/job-fields")
    public ApiResponseDto<JobFieldOptionListResponseDto> jobFields() {
        var dtos = jobFieldService.findAll()
            .stream()
            .map(JobFieldOptionResponseDto::from)
            .toList();

        var body = JobFieldOptionListResponseDto.of(dtos);

        return new ApiResponseDto<>(
            HttpStatus.OK.value(),
            HttpStatus.OK.name(),
            "전체 직군 조회 성공",
            body
        );
    }
}
