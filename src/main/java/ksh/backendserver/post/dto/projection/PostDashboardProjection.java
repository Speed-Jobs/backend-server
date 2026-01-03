package ksh.backendserver.post.dto.projection;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.jobrole.entity.JobRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostDashboardProjection {

    private Post post;
    private Company company;
    private JobRole jobRole;
    private LocalDateTime registeredAt;
}