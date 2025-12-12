package ksh.backendserver.post.entity;

import jakarta.persistence.*;
import ksh.backendserver.BaseEntity;
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
@SQLDelete(sql = "update post set is_deleted = true where id = ?")
@Where(clause = "is_deleted = false")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String employmentType;

    private String experience;

    private String workType;

    private LocalDateTime postedAt;

    private LocalDateTime closeAt;

    private LocalDateTime crawledAt;

    private String sourceUrl;

    private String screenshotUrl;

    private String description;

    private String urlHash;

    private Long companyId;

    private Long industryId;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted;
}
