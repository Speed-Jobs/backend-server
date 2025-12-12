package ksh.backendserver.post.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "근무 형태")
public enum WorkType {

    @Schema(description = "사무실 출근")
    ON_SITE,

    @Schema(description = "재택근무")
    REMOTE,

    @Schema(description = "하이브리드")
    HYBRID
}
