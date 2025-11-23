package ksh.backendserver.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GroupSharesResponseDto {

    //TODO: 이걸 내가 줘야 하는지 물어보기
    private List<String> legend;
    private List<GroupShareResponseDto> distributions;


    public static GroupSharesResponseDto of(List<GroupShareResponseDto> dtos) {
        return new GroupSharesResponseDto(dtos);
    }

    private GroupSharesResponseDto(List<GroupShareResponseDto> dtos) {
        this.distributions = dtos;
        this.legend = dtos.stream()
            .map(GroupShareResponseDto::getGroupName)
            .toList();
    }
}
