package ksh.backendserver.post.entity;

import jakarta.persistence.*;
import ksh.backendserver.BaseEntity;
import ksh.backendserver.post.enums.EmploymentType;
import ksh.backendserver.post.enums.ExperienceLevel;
import ksh.backendserver.post.enums.PostStatus;
import ksh.backendserver.post.enums.WorkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "update member set is_deleted = true where id = ?")
@Where(clause = "is_deleted = false")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    private WorkType workType;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    private LocalDateTime postedAt;

    private LocalDateTime closeAt;

    private String sourceUrl;

    private String screenshotUrl;

    private Long companyId;

    private Long roleId;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted;
}
