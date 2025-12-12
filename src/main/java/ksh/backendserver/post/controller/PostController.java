package ksh.backendserver.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ksh.backendserver.common.dto.request.PageRequestDto;
import ksh.backendserver.common.dto.response.ApiResponseDto;
import ksh.backendserver.common.dto.response.PageResponseDto;
import ksh.backendserver.post.dto.request.PostRequestDto;
import ksh.backendserver.post.dto.request.PostSummaryRequestDto;
import ksh.backendserver.post.dto.response.PostDetailResponseDto;
import ksh.backendserver.post.dto.response.PostSearchItemResponseDto;
import ksh.backendserver.post.dto.response.PostSummariesResponseDto;
import ksh.backendserver.post.dto.response.PostSummaryResponseDto;
import ksh.backendserver.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "채용공고", description = "채용공고 조회 API")
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(
        summary = "최근 공고 간단 조회",
        description = "특정 회사들의 최근 공고를 간단한 형태로 조회합니다. 생성일자 기준 내림차순 정렬되며, limit으로 조회 개수를 제한할 수 있습니다 (기본값: 10, 최소: 1, 최대: 20)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "limit 범위 초과 (1~20)")
    })
    @GetMapping("/api/v1/posts/simple")
    public ApiResponseDto<PostSummariesResponseDto> recentPostSummaries(
        @ParameterObject @Valid PostSummaryRequestDto request
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

    @Operation(
        summary = "경쟁사 공고 페이징 조회",
        description = "필터링 조건에 따라 경쟁사 공고를 페이징 조회합니다. 정렬 기준(sort), 정렬 방향(isAscending), 회사명, 연월, 공고 제목, 포지션명으로 필터링 가능합니다. 페이지 크기는 1~50 범위입니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "페이징 파라미터 유효성 실패"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 포지션명")
    })
    @GetMapping("/api/v1/posts")
    public ApiResponseDto<PageResponseDto<PostSearchItemResponseDto>> competitorPosts(
        @ParameterObject @Valid PostRequestDto postRequest,
        @ParameterObject @Valid PageRequestDto pageRequest
    ) {
        var page = postService.findCompetitorPosts(
                postRequest,
                pageRequest.toPageable()
            )
            .map(PostSearchItemResponseDto::from);

        var body = PageResponseDto.from(page);

        return ApiResponseDto.of(
            HttpStatus.OK.value(),
            HttpStatus.OK.name(),
            "공고 페이지 조회 성공",
            body
        );
    }

    @Operation(
        summary = "공고 상세 조회",
        description = "공고 ID로 공고 상세 정보를 조회합니다. 회사 정보, 포지션, 경력, 마감일, 지원 URL, 스크린샷, 필요 스킬 등을 포함합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 공고")
    })
    @GetMapping("/api/v1/posts/{postId}")
    public ApiResponseDto<PostDetailResponseDto> postDetail(
        @Parameter(description = "공고 ID", example = "1") @PathVariable Long postId
    ) {
        var postDetail = postService.getPostDetail(postId);
        var body = PostDetailResponseDto.from(postDetail);

        return ApiResponseDto.of(
            HttpStatus.OK.value(),
            HttpStatus.OK.name(),
            "경쟁사 공고 상세 조회 성공",
            body
        );
    }
}
