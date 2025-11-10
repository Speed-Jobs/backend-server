package ksh.backendserver.post.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryRequestDto {

    @Min(value = 1, message = "조회 개수는 최소 1개 입니다.")
    @Max(value = 20, message = "조회 개수는 최대 20개 입니다.")
    private Integer limit = 10;

    private List<Long> companyIds;
}
