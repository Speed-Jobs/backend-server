package ksh.backendserver.post.dto.request;

import jakarta.validation.constraints.NotNull;
import ksh.backendserver.post.enums.PostSortCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostRequestDto {

    private PostSortCriteria sort;

    @NotNull(message = "정렬 방향은 필수입니다.")
    private Boolean isAscending;

    private List<String> companyNames;

    private Integer year;

    private Integer month;

    private String postTitle;

    private String positionName;
}
