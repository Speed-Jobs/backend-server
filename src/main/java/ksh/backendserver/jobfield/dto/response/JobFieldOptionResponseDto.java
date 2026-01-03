package ksh.backendserver.jobfield.dto.response;

import ksh.backendserver.jobfield.entity.JobField;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobFieldOptionResponseDto {

    private long id;
    private String name;

    public static JobFieldOptionResponseDto from(JobField jobField) {
        return new JobFieldOptionResponseDto(
            jobField.getId(),
            jobField.getName()
        );
    }
}
