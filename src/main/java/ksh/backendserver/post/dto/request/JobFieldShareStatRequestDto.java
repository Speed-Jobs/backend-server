package ksh.backendserver.post.dto.request;

import jakarta.validation.constraints.NotNull;
import ksh.backendserver.company.enums.DateRange;
import ksh.backendserver.group.enums.JobFieldCategory;
import ksh.backendserver.post.enums.PostScope;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobFieldShareStatRequestDto {

    @NotNull(message = "공고 조회 범위는 필수입니다.")
    private PostScope scope;

    private String companyName;

    @NotNull(message = "직군 카테고리는 필수입니다.")
    private JobFieldCategory fieldCategory;

    @NotNull(message = "조회 기간은 필수입니다.")
    private DateRange dateRange;
}
