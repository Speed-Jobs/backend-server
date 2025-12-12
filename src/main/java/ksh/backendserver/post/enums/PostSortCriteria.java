package ksh.backendserver.post.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "공고 정렬 기준")
@Getter
@AllArgsConstructor
public enum PostSortCriteria {

    @Schema(description = "게시일")
    POST_AT,

    @Schema(description = "회사명")
    COMPANY_NAME,

    @Schema(description = "공고 제목")
    TITLE,

    @Schema(description = "마감까지 남은 일수")
    LEFT_DAYS
}
