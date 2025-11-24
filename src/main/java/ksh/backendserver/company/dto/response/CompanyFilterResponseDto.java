package ksh.backendserver.company.dto.response;

import ksh.backendserver.company.model.CompanyFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompanyFilterResponseDto {

    private long id;
    private String name;

    public static CompanyFilterResponseDto from(CompanyFilter companyInfo) {
        return new CompanyFilterResponseDto(companyInfo);
    }

    private CompanyFilterResponseDto(CompanyFilter companyInfo) {
        this.id = companyInfo.getId();
        this.name = companyInfo.getName();
    }
}
