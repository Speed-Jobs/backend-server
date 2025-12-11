package ksh.backendserver.industry.repository;

import ksh.backendserver.industry.entity.SubscriptionIndustry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionIndustryRepository extends JpaRepository<SubscriptionIndustry, Long> {
    List<SubscriptionIndustry> findByUserId(Long userId);
}