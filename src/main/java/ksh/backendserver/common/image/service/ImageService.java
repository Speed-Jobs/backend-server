package ksh.backendserver.common.image.service;

import ksh.backendserver.common.exception.CustomException;
import ksh.backendserver.common.exception.ErrorCode;
import ksh.backendserver.common.vo.ImageInfo;
import ksh.backendserver.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final String FORMAT_WEBP = "webp";
    private static final String MEDIA_TYPE_WEBP = "image/webp";

    private final PostRepository postRepository;

    @Value("${image.base-path}")
    private String basePath;

    @Value("${image.default-quality}")
    private double defaultQuality;

    public ImageInfo getPostScreenshot(long postId, Integer width, boolean useWebp) {
        String screenshotUrl = getScreenshotUrlFromPost(postId);
        Path filePath = validateAndGetPath(screenshotUrl);

        return processImage(filePath, width, useWebp);
    }

    private String getScreenshotUrlFromPost(long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND))
            .getScreenshotUrl();
    }

    private Path validateAndGetPath(String screenshotUrl) {
        Path filePath = Paths.get(basePath, screenshotUrl);

        if (!Files.exists(filePath)) {
            throw new CustomException(ErrorCode.IMAGE_NOT_FOUND, List.of(screenshotUrl));
        }

        return filePath;
    }

    private ImageInfo processImage(Path filePath, Integer width, boolean useWebp) {
        try {
            byte[] imageBytes = resizeAndConvertImage(filePath, width, useWebp);
            MediaType contentType = determineContentType(filePath, useWebp);
            return new ImageInfo(imageBytes, contentType);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private byte[] resizeAndConvertImage(Path filePath, Integer width, boolean useWebp) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        var builder = Thumbnails.of(filePath.toFile());

        if (width != null) {
            builder.width(width);
        } else {
            builder.scale(1.0);
        }

        builder.outputQuality(defaultQuality);

        if (useWebp) {
            builder.outputFormat(FORMAT_WEBP);
        }

        builder.toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    private MediaType determineContentType(Path filePath, boolean useWebp) throws IOException {
        if (useWebp) {
            return MediaType.parseMediaType(MEDIA_TYPE_WEBP);
        }

        return MediaType.parseMediaType(Files.probeContentType(filePath));
    }
}
