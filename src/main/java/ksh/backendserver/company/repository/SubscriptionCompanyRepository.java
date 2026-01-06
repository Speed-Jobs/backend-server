package ksh.backendserver.company.repository;

import ksh.backendserver.company.entity.SubscriptionCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionCompanyRepository extends JpaRepository<SubscriptionCompany, Long>, SubscriptionCompanyQueryRepository {
    List<SubscriptionCompany> findByUserId(Long userId);
    
    void deleteByUserId(Long userId);
}