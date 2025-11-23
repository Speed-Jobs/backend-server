package ksh.backendserver.post.dto.response;

import ksh.backendserver.post.model.JobFieldShare;
import lombok.AllArgsConstructor;
import lombok.Getter;

//TODO: 직군 직무 어느 용어가 편한지 프론트랑 얘기하기
@Getter
@AllArgsConstructor
public class JobFieldShareResponseDto {

    private long fieldId;
    private String fieldName;
    private double share;

    public static JobFieldShareResponseDto from(JobFieldShare model) {
        return new JobFieldShareResponseDto(model.getFieldId(), model.getFieldName(), model.getShare());
    }
}
