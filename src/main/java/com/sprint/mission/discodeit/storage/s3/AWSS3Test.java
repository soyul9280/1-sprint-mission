package com.sprint.mission.discodeit.storage.s3;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;

@Slf4j
public class AWSS3Test {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(Files.newInputStream(Paths.get(".env")));

        String accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
        String secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
        String region = properties.getProperty("AWS_S3_REGION");
        String bucket = properties.getProperty("AWS_S3_BUCKET");

        //aws에 요청할 client객체 생성=AWS S3와 상호작용하는 기본적인 동기식 클라이언트
        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(//s3사용자
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();

        log.info("업로드 테스트 시작");
        String key = "test.txt"; //s3에 저장될 경로
        s3.putObject(PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType("text/plain")
                        .build(),
                RequestBody.fromString("Test"));
        log.info("업로드 테스트 완료");

        log.info("다운로드 테스트 시작");
        //다운로드할 s3객체 지정
        ResponseBytes<GetObjectResponse> result = s3.getObject(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                ResponseTransformer.toBytes());
        log.info("다운로드 태스트 완료 :{} ", new String(result.asByteArray()));

        log.info("Presigned URL 생성 테스트 시작");
        //클라이언트가 직접 s3에 업로드 가능->서버 안거치고 직접 가능
        S3Presigner s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();

        GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))//5분동안 유효
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build())
                .build();
        String url = s3Presigner.presignGetObject(request).url().toString();
        log.info("Presigned URL 생성 테스트 완료: {}", url);
    }

}
