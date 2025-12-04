package ksh.backendserver.post.controller;

import ksh.backendserver.common.vo.ImageInfo;
import ksh.backendserver.common.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostImageController {

    private final ImageService imageService;

    @GetMapping("/api/v1/posts/{postId}/screenshot")
    public ResponseEntity<byte[]> getResizedImage(
        @PathVariable(name = "postId") long postId,
        @RequestParam(required = false) Integer width,
        @RequestParam(required = false, defaultValue = "false") boolean useWebp
    ) {
        ImageInfo image = imageService.getPostScreenshot(postId, width, useWebp);

        return ResponseEntity.ok()
            .contentType(image.getContentType())
            .body(image.getBytes());
    }
}
