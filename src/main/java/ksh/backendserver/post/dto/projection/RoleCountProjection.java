package ksh.backendserver.post.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoleCountProjection {

    private String roleName;
    private long postCount;
}
