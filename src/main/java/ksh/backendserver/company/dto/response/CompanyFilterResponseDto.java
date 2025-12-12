package ksh.backendserver.company.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ksh.backendserver.company.model.CompanyFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "회사 필터 응답")
@Getter
@AllArgsConstructor
public class CompanyFilterResponseDto {

    @Schema(description = "회사 ID", example = "1")
    private long id;

    @Schema(description = "회사명", example = "카카오")
    private String name;

    public static CompanyFilterResponseDto from(CompanyFilter companyInfo) {
        return new CompanyFilterResponseDto(companyInfo);
    }

    private CompanyFilterResponseDto(CompanyFilter companyInfo) {
        this.id = companyInfo.getId();
        this.name = companyInfo.getName();
    }
}
