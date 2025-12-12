package ksh.backendserver.company.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.company.dto.response.CompanyFilterResponseDto;
import ksh.backendserver.company.dto.response.CompanyResponseDto;
import ksh.backendserver.company.dto.response.CompanyFilterResponseDtos;
import ksh.backendserver.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회사", description = "회사 정보 조회 API")
@RestController
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @Operation(
        summary = "회사 필터 목록 조회",
        description = "필터링을 위한 회사 목록(ID, 이름)을 이름순으로 정렬하여 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/api/v1/companies/filter")
    public ApiResponseDto<CompanyFilterResponseDtos> getCompanyFilter() {
        var dtos = companyService.findCompanyFilters()
            .stream()
            .map(CompanyFilterResponseDto::from)
            .toList();

        var body = CompanyFilterResponseDtos.from(dtos);

        return ApiResponseDto.of(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                "회사 필터 목록 조회 성공",
                body
        );
    }
}
