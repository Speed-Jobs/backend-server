package ksh.backendserver.post.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import ksh.backendserver.common.vo.Date;
import ksh.backendserver.company.dto.response.CompanyResponseDto;
import ksh.backendserver.post.enums.EmploymentType;
import ksh.backendserver.post.enums.ExperienceLevel;
import ksh.backendserver.post.model.PostInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDto {

    private long id;
    private String title;
    private String role;
    private ExperienceLevel experience;
    private EmploymentType employmentType;
    private Date postedAt;
    private Date closeAt;
    private Date crawledAt;
    private Integer daysLeft;

    private CompanyResponseDto company;

    public static PostResponseDto from(PostInfo postInfo) {
        return new PostResponseDto(postInfo);
    }

    private PostResponseDto(PostInfo postInfo) {
        this.id = postInfo.getId();
        this.title = postInfo.getTitle();
        this.role = postInfo.getRole();
        this.experience = postInfo.getExperience();
        this.employmentType = postInfo.getEmploymentType();
        this.postedAt = Date.from(postInfo.getPostedAt().toLocalDate());
        this.closeAt = Date.from(postInfo.getClosedAt().toLocalDate());
        this.crawledAt = postInfo.getCrawledAt() != null
            ? Date.from(postInfo.getCrawledAt().toLocalDate())
            : null;
        this.daysLeft = postInfo.getDaysLeft();

        this.company = CompanyResponseDto.from(postInfo.getCompany());
    }
}
