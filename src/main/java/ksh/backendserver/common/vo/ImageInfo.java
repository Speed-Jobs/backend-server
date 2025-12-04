package ksh.backendserver.common.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.MediaType;

@Getter
@AllArgsConstructor
public class ImageInfo {

    private byte[] bytes;
    private MediaType contentType;
}
