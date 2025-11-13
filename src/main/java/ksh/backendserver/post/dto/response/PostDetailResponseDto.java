package ksh.backendserver.post.dto.response;

import ksh.backendserver.common.vo.DateTime;
import ksh.backendserver.company.dto.response.CompanyResponseDto;
import ksh.backendserver.post.model.PostDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostDetailResponseDto {

    private long id;
    private String title;
    private String role;
    private String experience;
    private int daysLeft;
    private DateTime postedAt;
    private DateTime closeAt;
    private String applyUrl;
    private List<String> skills;

    private CompanyResponseDto company;

    public static PostDetailResponseDto from(PostDetail postDetail) {
        return new PostDetailResponseDto(postDetail);
    }

    private PostDetailResponseDto(PostDetail postDetail) {
        this.id = postDetail.getId();
        this.title = postDetail.getTitle();
        this.role = postDetail.getRole();
        this.experience = postDetail.getExperience().name();
        this.daysLeft = postDetail.getDaysLeft();
        this.postedAt = DateTime.from(postDetail.getPostedAt());
        this.closeAt = DateTime.from(postDetail.getClosedAt());
        this.applyUrl = postDetail.getApplyUrl();
        this.skills = postDetail.getSkills();

        this.company = CompanyResponseDto.from(postDetail.getCompany());
    }
}