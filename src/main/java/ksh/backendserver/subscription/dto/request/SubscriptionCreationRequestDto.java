package ksh.backendserver.subscription.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import ksh.backendserver.notification.enums.NotificationType;
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

    @NotEmpty(message = "알림 수단을 최소 1개 이상 선택해야 합니다.")
    @Schema(description = "알림 수단 목록 (EMAIL, SLACK)", example = "[\"EMAIL\", \"SLACK\"]")
    private List<NotificationType> notificationTypes;
}
