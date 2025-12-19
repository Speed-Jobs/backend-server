package ksh.backendserver.company.dto.response;

import ksh.backendserver.company.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompanyOptionResponseDto {

    private long id;
    private String name;

    public static CompanyOptionResponseDto from(Company company) {
        return new CompanyOptionResponseDto(
            company.getId(),
            company.getName()
        );
    }
}
