package ksh.backendserver.post.dto.projection;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostWithCompany {

    private Post post;
    private Company company;
}
