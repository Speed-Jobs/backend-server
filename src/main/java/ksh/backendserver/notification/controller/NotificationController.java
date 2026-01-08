package ksh.backendserver.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ksh.backendserver.notification.facade.NotificationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
        notificationFacade.sendDailyNotifications();

        return ResponseEntity
            .noContent()
            .build();
    }

    @Operation(
        summary = "즉시 알림 전송",
        description = "크롤러가 새 공고 저장 후 호출하는 API. 해당 공고와 매칭되는 구독에게 즉시 알림 전송."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "알림 처리 완료")
    })
    @PostMapping("/api/posts/{postId}/notify")
    public ResponseEntity<Void> notifyNewPost(@PathVariable Long postId) {
        log.info("Received instant notification request for postId={}", postId);
        notificationFacade.notifyNewPost(postId);

        return ResponseEntity
            .noContent()
            .build();
    }
}
