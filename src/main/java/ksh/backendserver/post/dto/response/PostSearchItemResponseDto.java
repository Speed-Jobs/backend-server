package ksh.backendserver.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ksh.backendserver.common.vo.Date;
import ksh.backendserver.post.model.PostInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "공고 검색 결과 목록 응답")
@Getter
@AllArgsConstructor
public class PostSearchItemResponseDto {

    @Schema(description = "공고 ID", example = "1")
    private long id;

    @Schema(description = "공고 제목", example = "백엔드 개발자 채용")
    private String title;

    @Schema(description = "회사명", example = "SK AX")
    private String company;

    @Schema(description = "고용 형태", example = "FULL_TIME")
    private String employmentType;

    @Schema(description = "크롤링 일자")
    private Date crawledAt;

    public static PostSearchItemResponseDto from(PostInfo postInfo) {
        return new PostSearchItemResponseDto(postInfo);
    }

    private PostSearchItemResponseDto(PostInfo postInfo) {
        this.id = postInfo.getId();
        this.title = postInfo.getTitle();
        this.company = postInfo.getCompany().getName();
        this.employmentType = postInfo.getEmploymentType();
        this.crawledAt = postInfo.getCrawledAt() != null
            ? Date.from(postInfo.getCrawledAt().toLocalDate())
            : null;
    }
}
