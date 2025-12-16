package ksh.backendserver.company.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.company.dto.response.CompanyOptionListResponseDto;
import ksh.backendserver.company.dto.response.CompanyOptionResponseDto;
import ksh.backendserver.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회사", description = "회사 조회 API")
@RestController
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @Operation(
        summary = "경쟁사 목록 조회",
        description = "경쟁사로 등록된 회사 목록을 조회합니다. 주로 필터링 옵션으로 사용됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/api/v1/companies/competitor")
    public ApiResponseDto<CompanyOptionListResponseDto> competitors() {

        var dtos = companyService.findCompetitors()
            .stream()
            .map(CompanyOptionResponseDto::from)
            .toList();

        var body = CompanyOptionListResponseDto.of(dtos);

        return ApiResponseDto.of(
            HttpStatus.OK.value(),
            HttpStatus.OK.name(),
            "경쟁사 조회 성공",
            body
        );
    }
}
