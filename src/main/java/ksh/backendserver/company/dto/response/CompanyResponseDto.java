package ksh.backendserver.company.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ksh.backendserver.company.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "회사 정보 응답")
@Getter
@AllArgsConstructor
public class CompanyResponseDto {

    @Schema(description = "회사 ID", example = "1")
    private long id;

    @Schema(description = "회사명", example = "카카오")
    private String name;

    public static CompanyResponseDto from(Company company) {
        return new CompanyResponseDto(company);
    }

    private CompanyResponseDto(Company company) {
        this.id = company.getId();
        this.name = company.getName();
    }
}
