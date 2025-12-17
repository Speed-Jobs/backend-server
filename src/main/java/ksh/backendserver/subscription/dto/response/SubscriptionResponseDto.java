package ksh.backendserver.subscription.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ksh.backendserver.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Schema(description = "구독 정보 응답")
@Getter
@AllArgsConstructor
public class SubscriptionResponseDto {

    @Schema(description = "구독 중인 회사 이름 목록", example = "[\"카카오\", \"네이버\", \"토스\"]")
    private List<String> companyNames;

    @Schema(description = "구독 중인 기술 스택 이름 목록", example = "[\"Java\", \"Spring\", \"MySQL\"]")
    private List<String> skillNames;

    @Schema(description = "구독 중인 직군 이름 목록", example = "[\"백엔드 개발자\", \"프론트엔드 개발자\"]")
    private List<String> positionNames;

    @Schema(description = "알림 수단 목록", example = "[\"EMAIL\", \"SLACK\"]")
    private List<NotificationType> channels;

    public static SubscriptionResponseDto of(
        List<String> companyNames,
        List<String> skillNames,
        List<String> positionNames,
        List<NotificationType> notificationTypes
    ) {
        return new SubscriptionResponseDto(companyNames, skillNames, positionNames, notificationTypes);
    }
}
