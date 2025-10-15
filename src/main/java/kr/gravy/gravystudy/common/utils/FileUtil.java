package kr.gravy.gravystudy.common.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtil {

    /**
     * 파일명에서 확장자를 추출합니다.
     *
     * @param originalFileName 원본 파일명
     * @return 확장자 (예: ".jpg"), 없으면 빈 문자열
     * TODO:: 확장자 제약조건 리팩터링 필요
     */
    public String extractExtension(String originalFileName) {
        if (originalFileName == null || !originalFileName.contains(".")) {
            return "";
        }
        return originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
    }

    /**
     * S3에 저장할 고유한 파일명을 생성합니다.
     *
     * @param originalFileName 원본 파일명
     * @param folder           S3 폴더명
     * @return S3 key (예: "auctions/uuid.jpg")
     */
    public String generateS3Key(String originalFileName, String folder) {
        String extension = extractExtension(originalFileName);
        return folder + "/" + GeneratorUtil.generatePublicId() + extension;
    }
}
