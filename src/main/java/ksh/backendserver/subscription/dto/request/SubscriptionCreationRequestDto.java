package ksh.backendserver.subscription.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Schema(description = "구독 생성 요청")
@Getter
@AllArgsConstructor
public class SubscriptionCreationRequestDto {

    @Schema(description = "구독할 회사 ID 목록", example = "[1, 2, 3]")
    private List<Long> companyIds;

    @Schema(description = "구독할 기술 스택 ID 목록", example = "[1, 2, 3]")
    private List<Long> skillIds;

    @Schema(description = "구독할 직군 ID 목록", example = "[1, 2, 3]")
    private List<Long> positionIds;
}
