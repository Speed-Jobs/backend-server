package ksh.backendserver.post.model;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.post.dto.projection.PostDashboardProjection;
import ksh.backendserver.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostDashboardCard {

    private long id;
    private String title;
    private String role;
    private String employmentType;
    private LocalDateTime registeredAt;

    private Company company;

    public static PostDashboardCard from(PostDashboardProjection projection) {
        return new PostDashboardCard(projection);
    }

    private PostDashboardCard(PostDashboardProjection projection) {
        Post post = projection.getPost();

        this.id = post.getId();
        this.title = post.getTitle();
        this.role = projection.getJobRole().getName();
        this.employmentType = post.getEmploymentType();
        this.registeredAt = projection.getRegisteredAt();

        this.company = projection.getCompany();
    }
}