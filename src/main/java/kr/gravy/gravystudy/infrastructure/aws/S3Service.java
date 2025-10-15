package kr.gravy.gravystudy.infrastructure.aws;

import kr.gravy.gravystudy.common.exception.GravyException;
import kr.gravy.gravystudy.common.exception.Status;
import kr.gravy.gravystudy.common.utils.FileUtil;
import kr.gravy.gravystudy.configuration.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public static final String S3_FOLDER_NAME = "auctions";
    private static final int MAX_IMAGE_COUNT = 3;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * 파일을 S3에 업로드하고 S3 URL을 반환합니다.
     *
     * @param file 업로드할 파일
     * @return S3에 저장된 파일의 URL
     */
    public String uploadFile(MultipartFile file) {
        String key = FileUtil.generateS3Key(file.getOriginalFilename(), S3_FOLDER_NAME);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.bucketName())
                    .key(key)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ) // 공개 읽기 가능
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return s3Properties.getFileUrl(key);
        } catch (S3Exception e) {
            log.error("S3 업로드 실패 - 에러 코드: {}, 메시지: {}, 상태 코드: {}",
                    e.awsErrorDetails().errorCode(),
                    e.getMessage(),
                    e.statusCode());
            log.error("S3 상세 정보 - 버킷: {}, 키: {}, 리전: {}",
                    s3Properties.bucketName(),
                    key,
                    s3Properties.region());
            throw new GravyException(Status.S3_UPLOAD_FAILED);
        } catch (IOException e) {
            log.error("파일 읽기 실패: {}", e.getMessage());
            throw new GravyException(Status.FILE_READ_FAILED);
        }
    }

    /**
     * S3에서 파일을 삭제합니다.
     *
     * @param imageUrl 삭제할 S3 파일의 전체 URL
     */
    public void deleteFile(String imageUrl) {
        try {
            // URL에서 key 추출
            String key = imageUrl.substring(imageUrl.indexOf(S3_FOLDER_NAME));

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(s3Properties.bucketName())
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteRequest);
            log.info("S3 파일 삭제 성공");
        } catch (S3Exception e) {
            log.error("S3 파일 삭제 실패: {}, 원인: {}", imageUrl, e.getMessage());
        }

    }

    public void validatePossibleUploadImages(List<MultipartFile> images) {
        if (images.size() > MAX_IMAGE_COUNT) {
            throw new GravyException(Status.TOO_MANY_IMAGES);
        }
        for (MultipartFile image : images) {
            if (image.getSize() > MAX_FILE_SIZE) {
                throw new GravyException(Status.FILE_TOO_LARGE);
            }
        }
    }


}
