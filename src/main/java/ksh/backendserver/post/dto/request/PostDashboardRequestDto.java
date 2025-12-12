package ksh.backendserver.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "대시보드 공고 조회 요청")
@Getter
@AllArgsConstructor
public class PostDashboardRequestDto {

    @Schema(
        description = "조회 개수. 지정하지 않으면 10개를 조회합니다.",
        example = "10",
        minimum = "1",
        maximum = "50",
        nullable = true,
        defaultValue = "10"
    )
    @Min(value = 1, message = "조회 개수는 최소 1개 입니다.")
    @Max(value = 50, message = "조회 개수는 최대 50개 입니다.")
    private Integer limit;

    public int getLimit() {
        return limit == null ? 10 : limit;
    }
}