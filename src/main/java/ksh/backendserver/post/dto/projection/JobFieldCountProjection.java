package ksh.backendserver.post.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobFieldCountProjection {

    private long fieldId;
    private String fieldName;
    private long postCount;
}
