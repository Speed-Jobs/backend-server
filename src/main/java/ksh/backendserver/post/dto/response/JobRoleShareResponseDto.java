package ksh.backendserver.post.dto.response;

import ksh.backendserver.post.model.JobRoleShare;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobRoleShareResponseDto {

    private String name;
    private long count;
    private double share;

    public static JobRoleShareResponseDto from(JobRoleShare model) {
        return new JobRoleShareResponseDto(model.getName(), model.getCount(), model.getShare());
    }
}
