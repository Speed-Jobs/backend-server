package ksh.backendserver.jobfield.repository;

import java.util.List;

public interface SubscriptionJobFieldQueryRepository {
    List<String> findJobFieldNamesByUserId(Long userId);
}