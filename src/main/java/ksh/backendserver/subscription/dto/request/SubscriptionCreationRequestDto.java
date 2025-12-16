package ksh.backendserver.subscription.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Schema(description = "구독 생성 요청")
@Getter
@AllArgsConstructor
public class SubscriptionCreationRequestDto {

    @Schema(description = "사용자 ID", example = "1")
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long memberId;

    @Schema(description = "구독할 회사 ID 목록", example = "[1, 2, 3]")
    private List<Long> companyIds;

    @Schema(description = "구독할 기술 스택 ID 목록", example = "[1, 2, 3]")
    private List<Long> skillIds;

    @Schema(description = "구독할 직군 ID 목록", example = "[1, 2, 3]")
    private List<Long> positionIds;
}
