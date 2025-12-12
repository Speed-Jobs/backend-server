package ksh.backendserver.post.dto.projection;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.role.entity.Industry;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostDashboardProjection {

    private Post post;
    private Company company;
    private Industry jobRole;
    private LocalDateTime registeredAt;
}