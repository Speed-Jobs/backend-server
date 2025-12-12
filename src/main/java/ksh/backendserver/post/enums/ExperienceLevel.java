package ksh.backendserver.post.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "경력 수준")
public enum ExperienceLevel {

    @Schema(description = "신입")
    ENTRY,

    @Schema(description = "주니어")
    JUNIOR,

    @Schema(description = "중급/시니어")
    MID_SENIOR,

    @Schema(description = "시니어")
    SENIOR,

    @Schema(description = "리드/매니저")
    LEAD
}
