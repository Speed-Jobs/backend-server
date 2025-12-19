package ksh.backendserver.subscription.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.subscription.dto.request.SubscriptionCreationRequestDto;
import ksh.backendserver.subscription.dto.response.SubscriptionResponseDto;
import ksh.backendserver.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "구독", description = "공고 구독 관리 API")
@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(
        summary = "구독 저장",
        description = "사용자의 구독 정보를 저장합니다. 기존 구독을 모두 삭제하고 새로운 구독으로 교체합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "구독 저장 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패")
    })
    @PostMapping("/subscriptions")
    public ApiResponseDto<Void> saveSubscription(
        @Valid @RequestBody SubscriptionCreationRequestDto requestDto,
        HttpServletRequest httpRequest
    ) {
        HttpSession session = httpRequest.getSession();
        Long memberId = (Long) session.getAttribute("memberId");

        subscriptionService.save(requestDto, memberId);

        return ApiResponseDto.of(
            HttpStatus.OK.value(),
            HttpStatus.OK.name(),
            "구독이 저장되었습니다.",
            null
        );
    }

    @Operation(
        summary = "구독 취소",
        description = "사용자의 모든 활성화된 구독을 취소합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "구독 취소 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패")
    })
    @DeleteMapping("/subscriptions")
    public ApiResponseDto<Void> cancelSubscription(
        HttpServletRequest httpRequest
    ) {
        HttpSession session = httpRequest.getSession();
        Long memberId = (Long) session.getAttribute("memberId");

        subscriptionService.cancel(memberId);

        return ApiResponseDto.of(
            HttpStatus.OK.value(),
            HttpStatus.OK.name(),
            "구독이 취소되었습니다.",
            null
        );
    }

    @Operation(
        summary = "구독 조회",
        description = "사용자의 구독 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "구독 조회 성공"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패")
    })
    @GetMapping("/subscriptions")
    public ApiResponseDto<SubscriptionResponseDto> getSubscription(
        HttpServletRequest httpRequest
    ) {
        HttpSession session = httpRequest.getSession();
        Long memberId = (Long) session.getAttribute("memberId");

        var response = subscriptionService.findByMemberId(memberId);

        return ApiResponseDto.of(
            HttpStatus.OK.value(),
            HttpStatus.OK.name(),
            "구독 조회 성공",
            response
        );
    }
}
