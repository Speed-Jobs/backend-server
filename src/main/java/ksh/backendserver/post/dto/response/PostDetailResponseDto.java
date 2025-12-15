package ksh.backendserver.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ksh.backendserver.common.vo.DateTime;
import ksh.backendserver.company.dto.response.CompanyResponseDto;
import ksh.backendserver.post.model.PostDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Schema(description = "공고 상세 정보 응답")
@Getter
@AllArgsConstructor
public class PostDetailResponseDto {

    @Schema(description = "공고 ID", example = "1")
    private long id;

    @Schema(description = "공고 제목", example = "백엔드 개발자 채용")
    private String title;

    @Schema(description = "직무명", example = "백엔드 개발자")
    private String role;

    @Schema(description = "경력 수준", example = "신입")
    private String experience;

    @Schema(description = "고용 형태", example = "인턴")
    private String employmentType;

    @Schema(description = "마감까지 남은 일수", example = "7")
    private Integer daysLeft;

    @Schema(description = "게시 일시")
    private DateTime postedAt;

    @Schema(description = "마감 일시")
    private DateTime closeAt;

    @Schema(description = "지원 URL", example = "https://careers.kakao.com/jobs/1234")
    private String applyUrl;

    @Schema(description = "스크린샷 URL", example = "https://example.com/screenshot.png")
    private String screenShotUrl;

    @Schema(description = "필요 스킬 목록", example = "[\"Java\", \"Spring\", \"MySQL\"]")
    private List<String> skills;

    @Schema(description = "회사 정보")
    private CompanyResponseDto company;

    public static PostDetailResponseDto from(PostDetail postDetail) {
        return new PostDetailResponseDto(postDetail);
    }

    private PostDetailResponseDto(PostDetail postDetail) {
        this.id = postDetail.getId();
        this.title = postDetail.getTitle();
        this.role = postDetail.getRole();
        this.experience = postDetail.getExperience();
        this.employmentType = postDetail.getEmploymentType();
        this.daysLeft = postDetail.getDaysLeft();
        this.postedAt = DateTime.from(postDetail.getPostedAt());
        this.closeAt = DateTime.from(postDetail.getClosedAt());
        this.applyUrl = postDetail.getApplyUrl();
        this.screenShotUrl = postDetail.getScreenShotUrl();
        this.skills = postDetail.getSkills();

        this.company = CompanyResponseDto.from(postDetail.getCompany());
    }
}
