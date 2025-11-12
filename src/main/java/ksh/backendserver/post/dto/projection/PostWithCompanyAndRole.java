package ksh.backendserver.post.dto.projection;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.role.entity.JobRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostWithCompanyAndRole {

    private Post post;
    private Company company;
    private JobRole jobRole;
}
