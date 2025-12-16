package ksh.backendserver.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CompanyOptionListResponseDto {

    private List<CompanyOptionResponseDto> companies;

    public static CompanyOptionListResponseDto of(List<CompanyOptionResponseDto> companies) {
        return new CompanyOptionListResponseDto(companies);
    }
}
