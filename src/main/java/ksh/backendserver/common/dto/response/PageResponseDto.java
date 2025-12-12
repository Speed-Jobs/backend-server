package ksh.backendserver.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "페이징 응답")
@Getter
@Builder
public class PageResponseDto<T> {

    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    private long page;

    @Schema(description = "페이지 크기", example = "20")
    private long size;

    @Schema(description = "전체 페이지 수", example = "5")
    private long totalPages;

    @Schema(description = "페이지 내용 목록")
    private List<T> content;

    public static <T> PageResponseDto<T> from(Page<T> page) {
        return PageResponseDto.<T>builder()
            .page(page.getNumber())
            .size(page.getSize())
            .totalPages(page.getTotalPages())
            .content(page.getContent())
            .build();
    }
}
