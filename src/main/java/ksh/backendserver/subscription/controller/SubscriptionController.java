package ksh.backendserver.subscription.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.subscription.dto.request.SubscriptionCreationRequestDto;
import ksh.backendserver.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "구독", description = "공고 구독 관리 API")
@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(
        summary = "구독 생성",
        description = "사용자가 모니터링하고 싶은 회사, 기술 스택, 직군을 구독합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "구독 생성 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패")
    })
    @PostMapping("/subscriptions")
    public ApiResponseDto<Void> createSubscription(
        @Valid @RequestBody SubscriptionCreationRequestDto request
    ) {
        subscriptionService.create(request);

        return ApiResponseDto.of(
            HttpStatus.CREATED.value(),
            HttpStatus.CREATED.name(),
            "구독이 생성되었습니다.",
            null
        );
    }
}