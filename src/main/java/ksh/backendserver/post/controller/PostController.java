package ksh.backendserver.post.controller;

import jakarta.validation.Valid;
import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.company.dto.response.PostSummariesResponseDto;
import ksh.backendserver.post.dto.request.PostSummaryRequestDto;
import ksh.backendserver.post.dto.response.PostSummaryResponseDto;
import ksh.backendserver.post.model.PostSummary;
import ksh.backendserver.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/api/v1/posts/simple")
    public ApiResponseDto<PostSummariesResponseDto> recentPostSummaries(
        @Valid PostSummaryRequestDto request
    ) {
        var summaries = postService.findRecentPostSummaries(
                request.getCompanyIds(),
                request.getLimit()
            )
            .stream()
            .map(PostSummaryResponseDto::from)
            .toList();

        var body = PostSummariesResponseDto.of(summaries);

        return ApiResponseDto.of(
            HttpStatus.OK.value(),
            HttpStatus.OK.name(),
            "공고 간단 조회 성공",
            body
        );
    }
}
