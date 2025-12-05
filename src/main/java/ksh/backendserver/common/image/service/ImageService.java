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
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final String FORMAT_WEBP = "webp";
    private static final String MEDIA_TYPE_WEBP = "image/webp";

    private final PostRepository postRepository;
    private final S3Client s3Client;

    @Value("${aws.s3.access-point-arn}")
    private String accessPointArn;

    @Value("${aws.s3.prefix}")
    private String prefix;

    @Value("${image.default-quality}")
    private double defaultQuality;

    public ImageInfo getPostScreenshot(long postId, Integer width, boolean useWebp) {
        String imageKey = getImageKeyFromPost(postId);

        byte[] originalBytes = downloadFromS3(imageKey);

        byte[] processedBytes = resizeAndConvert(originalBytes, width, useWebp);
        MediaType contentType = determineContentType(useWebp);

        return new ImageInfo(processedBytes, contentType);
    }

    private String getImageKeyFromPost(long postId) {
        String imageKey = postRepository.findById(postId)
            .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND))
            .getScreenshotUrl();

        return prefix + imageKey;
    }

    private byte[] downloadFromS3(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(accessPointArn)
                .key(key)
                .build();

            ResponseBytes<GetObjectResponse> response = s3Client.getObject(
                getObjectRequest,
                ResponseTransformer.toBytes()
            );

            return response.asByteArray();
        } catch (NoSuchKeyException e) {
            throw new CustomException(ErrorCode.IMAGE_NOT_FOUND, List.of(key));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private byte[] resizeAndConvert(byte[] originalBytes, Integer width, boolean useWebp) {
        try {
            var inputStream = new ByteArrayInputStream(originalBytes);
            var outputStream = new ByteArrayOutputStream();

            var builder = Thumbnails.of(inputStream);

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
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private MediaType determineContentType(boolean useWebp) {
        return useWebp ?
            MediaType.parseMediaType(MEDIA_TYPE_WEBP) :
            MediaType.IMAGE_JPEG;
    }
}
