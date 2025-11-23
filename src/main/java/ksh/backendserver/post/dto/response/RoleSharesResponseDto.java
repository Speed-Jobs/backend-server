package ksh.backendserver.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoleSharesResponseDto {

    public List<RoleShareResponseDto> shares;

    public static RoleSharesResponseDto of(List<RoleShareResponseDto> dtos) {
        return new RoleSharesResponseDto(dtos);
    }
}
