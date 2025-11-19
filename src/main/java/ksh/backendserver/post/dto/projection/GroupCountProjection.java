package ksh.backendserver.post.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupCountProjection {

    private long groupId;
    private String groupName;
    private long postCount;
}