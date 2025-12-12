package ksh.backendserver.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ksh.backendserver.common.vo.Date;
import ksh.backendserver.post.model.PostDashboardCard;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "대시보드 공고 카드 응답")
@Getter
@AllArgsConstructor
public class PostDashboardCardResponseDto {

    @Schema(description = "공고 ID", example = "1")
    private long id;

    @Schema(description = "회사명", example = "카카오")
    private String companyName;

    @Schema(description = "공고 제목", example = "백엔드 개발자 채용")
    private String title;

    @Schema(description = "직무", example = "백엔드 개발자")
    private String role;

    @Schema(description = "등록일 (없으면 크롤링일)")
    private Date registeredAt;

    @Schema(description = "고용 형태", example = "FULL_TIME")
    private String employmentType;

    public static PostDashboardCardResponseDto from(PostDashboardCard card) {
        return new PostDashboardCardResponseDto(card);
    }

    private PostDashboardCardResponseDto(PostDashboardCard card) {
        this.id = card.getId();
        this.companyName = card.getCompany().getName();
        this.title = card.getTitle();
        this.role = card.getRole();
        this.registeredAt = Date.from(card.getRegisteredAt().toLocalDate());
        this.employmentType = card.getEmploymentType();
    }
}