package ksh.backendserver.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ksh.backendserver.common.image.service.ImageService;
import ksh.backendserver.common.vo.ImageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "채용공고 이미지", description = "채용공고 스크린샷 이미지 조회 API")
@RestController
@RequiredArgsConstructor
public class PostImageController {

    private final ImageService imageService;

    @Operation(
        summary = "공고 스크린샷 이미지 조회",
        description = "공고 ID로 스크린샷 이미지를 조회합니다. width 파라미터로 이미지 크기를 조정할 수 있으며, WebP 포맷으로 변환할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 공고 또는 이미지를 찾을 수 없음")
    })
    @GetMapping("/api/v1/posts/{postId}/screenshot")
    public ResponseEntity<byte[]> getResizedImage(
        @Parameter(description = "공고 ID", example = "1") @PathVariable(name = "postId") long postId,
        @Parameter(description = "이미지 너비 (픽셀). 지정하지 않으면 원본 크기를 반환합니다.", example = "800") @RequestParam(required = false) Integer width,
        @Parameter(description = "WebP 포맷 사용 여부", example = "false") @RequestParam(required = false, defaultValue = "false") boolean useWebp
    ) {
        ImageInfo image = imageService.getPostScreenshot(postId, width, useWebp);

        return ResponseEntity.ok()
            .contentType(image.getContentType())
            .body(image.getBytes());
    }
}
