package ksh.backendserver.company.service;

import ksh.backendserver.company.model.CompanyFilter;
import ksh.backendserver.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<CompanyFilter> findCompanyFilters() {
        return companyRepository.findAllByOrderByNameAsc()
            .stream()
            .map(CompanyFilter::from)
            .toList();
    }
}
