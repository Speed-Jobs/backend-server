package ksh.backendserver.post.model;

import ksh.backendserver.post.dto.projection.JobRoleCountProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobRoleShare {

    private String name;
    private long count;
    private double share;

    public static JobRoleShare from(JobRoleCountProjection projection, long totalPostCount) {
        double share = (double) projection.getPostCount() / totalPostCount * 100;
        double roundedShare = Math.round(share * 10) / 10.0;

        return new JobRoleShare(
            projection.getRoleName(),
            projection.getPostCount(),
            roundedShare
        );
    }
}
