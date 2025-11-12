package ksh.backendserver.post.dto.response;

import ksh.backendserver.common.vo.Date;
import ksh.backendserver.company.dto.response.CompanyResponseDto;
import ksh.backendserver.post.enums.ExperienceLevel;
import ksh.backendserver.post.enums.WorkType;
import ksh.backendserver.post.model.PostInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostResponseDto {

    private long id;
    private String title;
    private String role;
    private ExperienceLevel experience;
    private Date postedAt;
    private Date closeAt;
    private WorkType workType;
    private int daysLeft;

    private CompanyResponseDto company;

    public static PostResponseDto from(PostInfo postInfo) {
        return new PostResponseDto(postInfo);
    }

    private PostResponseDto(PostInfo postInfo) {
        this.id = postInfo.getId();
        this.title = postInfo.getTitle();
        this.role = postInfo.getRole();
        this.experience = postInfo.getExperience();
        this.postedAt = Date.from(postInfo.getPostedAt().toLocalDate());
        this.closeAt = Date.from(postInfo.getClosedAt().toLocalDate());
        this.workType = postInfo.getWorkType();
        this.daysLeft = postInfo.getDaysLeft();

        this.company = CompanyResponseDto.from(postInfo.getCompany());
    }
}
