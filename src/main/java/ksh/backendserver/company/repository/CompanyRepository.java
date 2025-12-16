package ksh.backendserver.company.repository;

import ksh.backendserver.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByIsCompetitor(boolean isCompetitor);
}
