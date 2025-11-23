package ksh.backendserver.post.dto.response;

import ksh.backendserver.post.model.RoleShare;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoleShareResponseDto {

    private String name;
    private long count;
    private double share;

    public static RoleShareResponseDto from(RoleShare model) {
        return new RoleShareResponseDto(model.getName(), model.getCount(), model.getShare());
    }
}
