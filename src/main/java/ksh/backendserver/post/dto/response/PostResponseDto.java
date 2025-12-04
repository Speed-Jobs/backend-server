package ksh.backendserver.post.dto.response;

import ksh.backendserver.common.vo.Date;
import ksh.backendserver.post.model.PostInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostResponseDto {

    private long id;
    private String title;
    private String employmentType;
    private Date crawledAt;

    public static PostResponseDto from(PostInfo postInfo) {
        return new PostResponseDto(postInfo);
    }

    private PostResponseDto(PostInfo postInfo) {
        this.id = postInfo.getId();
        this.title = postInfo.getTitle();
        this.employmentType = postInfo.getEmploymentType();
        this.crawledAt = postInfo.getCrawledAt() != null
            ? Date.from(postInfo.getCrawledAt().toLocalDate())
            : null;
    }
}
