package ksh.backendserver.post.model;

import ksh.backendserver.post.dto.projection.JobFieldCountProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobFieldShare {

    private long fieldId;
    private String fieldName;
    private double share;

    public static JobFieldShare from(JobFieldCountProjection projection, long totalPostCount) {
        double share = (double) projection.getPostCount() / totalPostCount * 100;
        double roundedShare = Math.round(share * 10) / 10.0;

        return new JobFieldShare(
            projection.getFieldId(),
            projection.getFieldName(),
            roundedShare
        );
    }
}
