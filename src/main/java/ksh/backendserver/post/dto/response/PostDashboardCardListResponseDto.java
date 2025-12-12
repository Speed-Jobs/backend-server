package ksh.backendserver.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostDashboardCardListResponseDto {

    List<PostDashboardCardResponseDto> posts;

    public static PostDashboardCardListResponseDto of(List<PostDashboardCardResponseDto> posts) {
        return new PostDashboardCardListResponseDto(posts);
    }
}
