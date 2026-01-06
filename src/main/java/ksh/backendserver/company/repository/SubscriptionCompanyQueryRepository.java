package ksh.backendserver.company.repository;

import java.util.List;

public interface SubscriptionCompanyQueryRepository {
    List<String> findCompanyNamesByUserId(Long userId);
}