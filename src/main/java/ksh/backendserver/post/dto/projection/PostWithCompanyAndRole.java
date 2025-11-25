package ksh.backendserver.post.dto.projection;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.role.entity.Industry;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostWithCompanyAndRole {

    private Post post;
    private Company company;
    private Industry jobRole;
}
