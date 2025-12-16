package ksh.backendserver.company.controller;

import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.company.dto.response.CompanyOptionListResponseDto;
import ksh.backendserver.company.dto.response.CompanyOptionResponseDto;
import ksh.backendserver.company.service.CompanyService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/api/v1/companies/competitor")
    private ApiResponseDto<CompanyOptionListResponseDto> competitors() {

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
