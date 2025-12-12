package ksh.backendserver.post.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "고용 형태")
public enum EmploymentType {

    @Schema(description = "정규직")
    FULL_TIME,

    @Schema(description = "계약직")
    CONTRACT,

    @Schema(description = "인턴")
    INTERN,

    @Schema(description = "프리랜서")
    FREELANCER,

    @Schema(description = "파트타임")
    PART_TIME,
}
