package ksh.backendserver.group.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.group.dto.response.PositionOptionListResponseDto;
import ksh.backendserver.group.dto.response.PositionOptionResponseDto;
import ksh.backendserver.group.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "직군", description = "직군 조회 API")
@RestController
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @Operation(
        summary = "전체 직군 목록 조회",
        description = "전체 직군 목록을 조회합니다. 주로 필터링 옵션으로 사용됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/api/v1/positions")
    public ApiResponseDto<PositionOptionListResponseDto> positions() {
        var dtos = positionService.findAll()
            .stream()
            .map(PositionOptionResponseDto::from)
            .toList();

        var body = PositionOptionListResponseDto.of(dtos);

        return new ApiResponseDto<>(
            HttpStatus.OK.value(),
            HttpStatus.OK.name(),
            "전체 직군 조회 성공",
            body
        );
    }
}
