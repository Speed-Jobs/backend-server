package ksh.backendserver.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CompanyResponseDtos {

    private List<CompanyResponseDto> companies;

    public static CompanyResponseDtos from(List<CompanyResponseDto> dtos) {
        return new CompanyResponseDtos(dtos);
    }
}
