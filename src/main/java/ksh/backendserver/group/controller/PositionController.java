package ksh.backendserver.group.controller;

import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.group.dto.response.PositionOptionListResponseDto;
import ksh.backendserver.group.dto.response.PositionOptionResponseDto;
import ksh.backendserver.group.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

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
