package ksh.backendserver.jobfield.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class JobFieldOptionListResponseDto {

    private List<JobFieldOptionResponseDto> options;

    public static JobFieldOptionListResponseDto of(List<JobFieldOptionResponseDto> options) {
        return new JobFieldOptionListResponseDto(options);
    }
}
