package ksh.backendserver.company.controller;

import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.company.dto.response.CompanyResponseDto;
import ksh.backendserver.company.dto.response.CompanyResponseDtos;
import ksh.backendserver.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/api/v1/companies/filter")
    public ApiResponseDto<CompanyResponseDtos> getCompanyFilter() {
        var dtos = companyService.findCompanyFilters()
            .stream()
            .map(CompanyResponseDto::from)
            .toList();

        var body = CompanyResponseDtos.from(dtos);

        return ApiResponseDto.of(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                "회사 필터 목록 조회 성공",
                body
        );
    }
}
