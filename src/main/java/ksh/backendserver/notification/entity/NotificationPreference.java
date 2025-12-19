package ksh.backendserver.notification.entity;

import jakarta.persistence.*;
import ksh.backendserver.BaseEntity;
import ksh.backendserver.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "update notification_preference set is_deleted = true where id = ?")
@Where(clause = "is_deleted = false")
public class NotificationPreference extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(50)")
    private NotificationType notificationType;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted;
}
