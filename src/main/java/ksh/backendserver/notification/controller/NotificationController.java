package ksh.backendserver.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ksh.backendserver.notification.facade.NotificationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "알림", description = "알림 관리 API")
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationFacade notificationFacade;

    @Operation(
        summary = "알림 전송 테스트",
        description = "스케줄러를 기다리지 않고 즉시 알림 전송 로직을 실행합니다. 성능 테스트 용도."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "알림 전송 완료 (로그 확인)")
    })
    @PostMapping("/api/notifications/test")
    public ResponseEntity<Void> testNotifications() {
        notificationFacade.sendNotifications();

        return ResponseEntity
            .noContent()
            .build();
    }
}
