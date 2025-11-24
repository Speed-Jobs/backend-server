package ksh.backendserver.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class JobRoleSharesResponseDto {

    public List<JobRoleShareResponseDto> shares;

    public static JobRoleSharesResponseDto of(List<JobRoleShareResponseDto> dtos) {
        return new JobRoleSharesResponseDto(dtos);
    }
}
