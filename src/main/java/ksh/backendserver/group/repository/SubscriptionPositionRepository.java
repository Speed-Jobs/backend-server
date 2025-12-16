package ksh.backendserver.group.repository;

import ksh.backendserver.group.entity.SubscriptionPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionPositionRepository extends JpaRepository<SubscriptionPosition, Long> {
    List<SubscriptionPosition> findByUserId(Long userId);
}
