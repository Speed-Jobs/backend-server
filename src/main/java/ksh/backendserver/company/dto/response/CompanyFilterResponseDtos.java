package ksh.backendserver.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CompanyFilterResponseDtos {

    private List<CompanyFilterResponseDto> companies;

    public static CompanyFilterResponseDtos from(List<CompanyFilterResponseDto> dtos) {
        return new CompanyFilterResponseDtos(dtos);
    }
}
