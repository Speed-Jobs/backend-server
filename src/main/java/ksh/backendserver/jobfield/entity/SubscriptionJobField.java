package ksh.backendserver.jobfield.entity;

import jakarta.persistence.*;
import ksh.backendserver.BaseEntity;
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
@Table(name = "subscription_job_field")
@SQLDelete(sql = "update subscription_job_field set is_deleted = true where id = ?")
@Where(clause = "is_deleted = false")
public class SubscriptionJobField extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long jobFieldId;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted;
}
