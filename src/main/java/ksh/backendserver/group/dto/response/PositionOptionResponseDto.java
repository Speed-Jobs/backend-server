package ksh.backendserver.group.dto.response;

import ksh.backendserver.group.entity.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PositionOptionResponseDto {

    private long id;
    private String name;

    public static PositionOptionResponseDto from(Position position) {
        return new PositionOptionResponseDto(
            position.getId(),
            position.getName()
        );
    }
}
