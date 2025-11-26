package ksh.backendserver.post.model;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.post.dto.projection.PostWithCompanyAndRole;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.post.enums.ExperienceLevel;
import ksh.backendserver.role.entity.Industry;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostDetail {

    private long id;
    private String title;
    private String role;
    private ExperienceLevel experience;
    private int daysLeft;
    private LocalDateTime postedAt;
    private LocalDateTime closedAt;
    private String applyUrl;
    private String screenShotUrl;
    private List<String> skills;

    private Company company;

    public static PostDetail from(PostWithCompanyAndRole projection, List<String> skillNames, LocalDate now) {
        return new PostDetail(projection, skillNames, now);
    }

    private PostDetail(PostWithCompanyAndRole projection, List<String> skillNames, LocalDate now) {
        Post post = projection.getPost();
        Industry jobRole = projection.getJobRole();

        this.id = post.getId();
        this.title = post.getTitle();
        this.role = jobRole.getName();
        this.experience = post.getExperience();
        this.daysLeft = calculateDaysLeft(post.getCloseAt(), now);
        this.postedAt = post.getPostedAt();
        this.closedAt = post.getCloseAt();
        this.applyUrl = post.getSourceUrl();
        this.screenShotUrl = post.getScreenshotUrl();
        this.skills = skillNames;

        this.company = projection.getCompany();
    }

    private int calculateDaysLeft(LocalDateTime closeAt, LocalDate now) {
        LocalDate closeDate = closeAt.toLocalDate();
        return (int) (closeDate.toEpochDay() - now.toEpochDay());
    }
}
