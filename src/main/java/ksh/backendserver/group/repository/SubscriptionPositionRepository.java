package ksh.backendserver.group.repository;

import ksh.backendserver.group.entity.SubscriptionPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPositionRepository extends JpaRepository<SubscriptionPosition, Long> {
}
