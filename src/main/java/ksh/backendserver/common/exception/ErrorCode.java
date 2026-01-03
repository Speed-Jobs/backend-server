package ksh.backendserver.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    MEMBER_NOT_FOUND(404, "member.not.found"),
    MEMBER_DUPLICATE_EMAIL(400, "member.duplicate.email"),
    MEMBER_PASSWORD_MISMATCH(400, "member.password.mismatch"),
    POST_NOT_FOUND(404, "post.not.found"),
    JOB_FIELD_NOT_FOUND(404, "jobfield.not.found"),
    COMPANY_NOT_FOUND(404, "company.not.found"),
    IMAGE_NOT_FOUND(404, "image.not.found"),
    INTERNAL_SERVER_ERROR(500, "internal.server.error");

    private final int status;
    private final String messageKey;
}
