package ksh.backendserver.jobfield.entity;

import jakarta.persistence.*;
import ksh.backendserver.BaseEntity;
import ksh.backendserver.jobfield.enums.JobFieldCategory;
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
@Table(name = "job_field")
@SQLDelete(sql = "update job_field set is_deleted = true where id = ?")
@Where(clause = "is_deleted = false")
public class JobField extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private JobFieldCategory category;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted;
}
