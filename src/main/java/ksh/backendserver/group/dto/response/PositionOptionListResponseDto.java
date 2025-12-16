package ksh.backendserver.group.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PositionOptionListResponseDto {

    private List<PositionOptionResponseDto> options;

    public static PositionOptionListResponseDto of(List<PositionOptionResponseDto> options) {
        return new PositionOptionListResponseDto(options);
    }
}
