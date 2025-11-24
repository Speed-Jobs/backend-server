package ksh.backendserver.company.dto.response;

import ksh.backendserver.company.entity.Company;
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

    private CompanyResponseDto(Company company) {
        this.id = company.getId();
        this.name = company.getName();
    }
}
