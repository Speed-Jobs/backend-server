package ksh.backendserver.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class JobFieldSharesResponseDto {

    //TODO: 이걸 내가 줘야 하는지 물어보기
    private List<String> legend;
    private List<JobFieldShareResponseDto> distributions;


    public static JobFieldSharesResponseDto of(List<JobFieldShareResponseDto> dtos) {
        return new JobFieldSharesResponseDto(dtos);
    }

    private JobFieldSharesResponseDto(List<JobFieldShareResponseDto> dtos) {
        this.distributions = dtos;
        this.legend = dtos.stream()
            .map(JobFieldShareResponseDto::getFieldName)
            .toList();
    }
}
