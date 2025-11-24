package ksh.backendserver.company.dto.response;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.company.model.CompanyFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompanyResponseDto {

    private long id;
    private String name;

    public static CompanyResponseDto from(Company company) {
        return new CompanyResponseDto(company);
    }

    public static CompanyResponseDto from(CompanyFilter companyInfo) {
        return new CompanyResponseDto(companyInfo);
    }

    private CompanyResponseDto(Company company) {
        this.id = company.getId();
        this.name = company.getName();
    }

    private CompanyResponseDto(CompanyFilter companyInfo) {
        this.id = companyInfo.getId();
        this.name = companyInfo.getName();
    }
}
