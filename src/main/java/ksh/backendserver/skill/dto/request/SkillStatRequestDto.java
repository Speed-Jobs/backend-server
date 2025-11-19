package ksh.backendserver.skill.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import ksh.backendserver.company.enums.DateRange;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkillStatRequestDto {

    @NotNull(message = "조회할 기술 개수는 필수입니다.")
    @Max(value = 15, message = "기술은 최대 15개까지 조회할 수 있습니다.")
    private Integer size;

    @NotNull(message = "차트의 시간 단위는 필수입니다.")
    private DateRange dateRange;
}
