package ksh.backendserver.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ksh.backendserver.post.enums.PostSortCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Schema(description = "경쟁사 공고 필터 및 정렬 조회 요청")
@Getter
@AllArgsConstructor
public class PostRequestDto {

    @Schema(
        description = "정렬 기준",
        example = "POST_AT",
        nullable = true,
        allowableValues = {"POST_AT", "COMPANY_NAME", "TITLE", "LEFT_DAYS"}
    )
    private PostSortCriteria sort;

    @Schema(
        description = "정렬 방향 (true: 오름차순, false: 내림차순)",
        example = "false",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "정렬 방향은 필수입니다.")
    private Boolean isAscending;

    @Schema(
        description = "필터링할 회사명 목록. 지정하지 않으면 전체 회사 기준으로 조회합니다.",
        example = "[\"카카오\", \"네이버\"]",
        nullable = true
    )
    private List<String> companyNames;

    @Schema(
        description = "필터링할 연도. 지정하지 않으면 전체 연도를 대상으로 조회합니다.",
        example = "2024",
        nullable = true
    )
    private Integer year;

    @Schema(
        description = "필터링할 월 (1~12). 지정하지 않으면 전체 월을 대상으로 조회합니다.",
        example = "12",
        nullable = true
    )
    private Integer month;

    @Schema(
        description = "공고 제목 검색어 (부분 일치). 지정하지 않으면 제목 필터링을 적용하지 않습니다.",
        example = "백엔드",
        nullable = true
    )
    private String postTitle;

    @Schema(
        description = "직군명 필터 (정확히 일치). 지정하지 않으면 직군 필터링을 적용하지 않습니다.",
        example = "백엔드 개발자",
        nullable = true
    )
    private String jobFieldName;
}
