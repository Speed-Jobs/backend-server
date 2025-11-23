package ksh.backendserver.post.model;

import ksh.backendserver.post.dto.projection.RoleCountProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoleShare {

    private String name;
    private long count;
    private double share;

    public static RoleShare from(RoleCountProjection projection, long totalPostCount) {
        double share = (double) projection.getPostCount() / totalPostCount * 100;
        double roundedShare = Math.round(share * 10) / 10.0;

        return new RoleShare(
            projection.getRoleName(),
            projection.getPostCount(),
            roundedShare
        );
    }
}
