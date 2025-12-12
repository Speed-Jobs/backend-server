package ksh.backendserver.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Schema(description = "공고 간단 조회 요청")
@Getter
@AllArgsConstructor
public class PostSummaryRequestDto {

    @Schema(
        description = "조회 개수. 지정하지 않으면 10개를 조회합니다.",
        example = "10",
        minimum = "1",
        maximum = "20",
        nullable = true,
        defaultValue = "10"
    )
    @Min(value = 1, message = "조회 개수는 최소 1개 입니다.")
    @Max(value = 20, message = "조회 개수는 최대 20개 입니다.")
    private Integer limit;

    @Schema(
        description = "필터링할 회사 ID 목록. 지정하지 않으면 전체 회사의 공고를 조회합니다.",
        example = "[1, 2, 3]",
        nullable = true
    )
    private List<Long> companyIds;

    public int getLimit() {
        return limit == null ? 10 : limit;
    }
}
