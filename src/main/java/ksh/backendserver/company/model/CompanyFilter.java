package ksh.backendserver.company.model;

import ksh.backendserver.company.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompanyFilter {

    private Long id;
    private String name;

    public static CompanyFilter from(Company company) {
        return new CompanyFilter(company.getId(), company.getName());
    }
}
