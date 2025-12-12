package ksh.backendserver.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ksh.backendserver.company.dto.response.CompanyResponseDto;
import ksh.backendserver.post.model.PostSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "공고 간단 정보 응답")
@Getter
@AllArgsConstructor
public class PostSummaryResponseDto {

    @Schema(description = "공고 ID", example = "1")
    private long id;

    @Schema(description = "공고 제목", example = "백엔드 개발자 채용")
    private String title;

    @Schema(description = "직무명", example = "백엔드 개발자")
    private String role;

    @Schema(description = "경력 수준", example = "MID_SENIOR")
    private String experience;

    @Schema(description = "마감까지 남은 일수", example = "7")
    private int daysLeft;

    @Schema(description = "회사 정보")
    private CompanyResponseDto company;

    public static PostSummaryResponseDto from(PostSummary summary) {
        return new PostSummaryResponseDto(summary);
    }

    private PostSummaryResponseDto(PostSummary summary) {
        this.id = summary.getId();
        this.title = summary.getTitle();
        this.role = summary.getRole();
        this.experience = summary.getExperience();
        this.daysLeft = summary.getDaysLeft();

        this.company = CompanyResponseDto.from(summary.getCompany());
    }
}
