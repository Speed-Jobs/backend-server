package ksh.backendserver.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostSummariesResponseDto {

    private List<PostSummaryResponseDto> posts;

    public static PostSummariesResponseDto of(List<PostSummaryResponseDto> dtos) {
        return new PostSummariesResponseDto(dtos);
    }
}
