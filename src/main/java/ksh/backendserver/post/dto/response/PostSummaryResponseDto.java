package ksh.backendserver.post.dto.response;

import ksh.backendserver.company.dto.response.CompanyResponseDto;
import ksh.backendserver.post.model.PostSummary;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostSummaryResponseDto {

    private long id;
    private String title;
    private String role;
    private String experience;
    private int daysLeft;

    private CompanyResponseDto company;

    public static PostSummaryResponseDto from(PostSummary summary) {
        return new PostSummaryResponseDto(summary);
    }

    private PostSummaryResponseDto(PostSummary summary) {
        this.id = summary.getId();
        this.title = summary.getTitle();
        this.role = summary.getRole();
        this.experience = summary.getExperience().name();
        this.daysLeft = summary.getDaysLeft();

        this.company = CompanyResponseDto.from(summary.getCompany());
    }
}
