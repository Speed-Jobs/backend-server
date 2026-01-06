package ksh.backendserver.notification.repository;

import ksh.backendserver.notification.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {

    List<NotificationPreference> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);
}
