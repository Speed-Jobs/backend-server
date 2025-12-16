package ksh.backendserver.company.service;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.company.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public List<Company> findCompetitors() {
        return companyRepository.findByIsCompetitor(true);
    }
}
