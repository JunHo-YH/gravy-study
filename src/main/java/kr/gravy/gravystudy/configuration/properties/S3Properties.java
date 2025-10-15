package kr.gravy.gravystudy.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
public record S3Properties(
        String accessKeyId,
        String secretAccessKey,
        String region,
        String bucketName,
        String baseUrl
) {
    private static final String DEFAULT_S3_URL_FORMAT = "https://%s.s3.%s.amazonaws.com/%s";

    public String getFileUrl(String key) {
        if (baseUrl != null && !baseUrl.isBlank()) {
            return baseUrl.endsWith("/") ? baseUrl + key : baseUrl + "/" + key;
        }
        return String.format(DEFAULT_S3_URL_FORMAT, bucketName, region, key);
    }
}
