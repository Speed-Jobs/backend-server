package ksh.backendserver.post.model;

import ksh.backendserver.post.dto.projection.GroupCountProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupShare {

    private long groupId;
    private String groupName;
    private double share;

    public static GroupShare from(GroupCountProjection projection, long totalPostCount) {
        double share = (double) projection.getPostCount() / totalPostCount * 100;
        double roundedShare = Math.round(share * 10) / 10.0;

        return new GroupShare(
            projection.getGroupId(),
            projection.getGroupName(),
            roundedShare
        );
    }
}