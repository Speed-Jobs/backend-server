package ksh.backendserver.jobfield.repository;

import ksh.backendserver.jobfield.entity.SubscriptionJobField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionJobFieldRepository extends JpaRepository<SubscriptionJobField, Long> {
    List<SubscriptionJobField> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
