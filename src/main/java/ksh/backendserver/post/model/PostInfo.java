package ksh.backendserver.post.model;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.post.dto.projection.PostWithCompanyAndRole;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.post.enums.EmploymentType;
import ksh.backendserver.post.enums.ExperienceLevel;
import ksh.backendserver.role.entity.Industry;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostInfo {

    private long id;
    private String title;
    private String role;
    private ExperienceLevel experience;
    private int daysLeft;
    private EmploymentType employmentType;
    private LocalDateTime postedAt;
    private LocalDateTime closedAt;
    private LocalDateTime crawledAt;

    private Company company;

    public static PostInfo from(PostWithCompanyAndRole projection, LocalDate now) {
        return new PostInfo(projection, now);
    }

    private PostInfo(PostWithCompanyAndRole projection, LocalDate now) {
        Post post = projection.getPost();
        Industry jobRole = projection.getJobRole();

        this.id = post.getId();
        this.title = post.getTitle();
        this.role = jobRole.getName();
        this.experience = post.getExperience();
        this.daysLeft = calculateDaysLeft(post.getCloseAt(), now);
        this.employmentType = post.getEmploymentType();
        this.postedAt = post.getPostedAt();
        this.closedAt = post.getCloseAt();
        this.crawledAt = post.getCrawledAt();

        this.company = projection.getCompany();
    }

    private int calculateDaysLeft(LocalDateTime closeAt, LocalDate now) {
        LocalDate closeDate = closeAt.toLocalDate();
        return (int) (closeDate.toEpochDay() - now.toEpochDay());
    }
}
