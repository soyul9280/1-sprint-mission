package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class S3BinaryContentStorage implements BinaryContentStorage {
    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;

    @Override
    public UUID put(UUID id, byte[] data) {
        String key=id.toString();
        log.debug("업로드 시작 요청: id={}, size={}", id, data.length);
        S3Client s3=getS3Client();
        s3.putObject(PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentLength((long) data.length)
                .build(),
        RequestBody.fromBytes(data));
        log.info("s3 업로드 완료:{}", key);
        return id;
    }

    @Override
    public InputStream get(UUID id) {
        log.debug("s3 파일 get 요청: id={}", id);
        String key = id.toString();
        S3Client s3 = getS3Client();
        ResponseBytes<GetObjectResponse> result = s3.getObject(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                ResponseTransformer.toBytes());
        log.info("s3파일 get 요청 완료");
        return new ByteArrayInputStream(result.asByteArray());
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(binaryContentDto.getId().toString())
                .responseContentType(binaryContentDto.getContentType())
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(request)
                .signatureDuration(Duration.ofMinutes(10))
                .build();
        PresignedGetObjectRequest presignRequestResult = presigner.presignGetObject(presignRequest);
        presigner.close();
        return ResponseEntity.status(302)
                .header("location",presignRequestResult.url().toString())
                .build();
    }


    private S3Client getS3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey,secretKey)))
                .build();
    }


}
