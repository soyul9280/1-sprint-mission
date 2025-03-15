package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Slf4j
@NoArgsConstructor
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private Path root=Paths.get(System.getProperty("user.dir"));

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }

    @PostConstruct
    public void init() {
        if(!Files.exists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                throw new RuntimeException("폴더 초기화 실패",e);
            }
        }
    }

    @Override
    public UUID put(UUID id, byte[] data) {
        Path filePath=resolvePath(id);
        try{
            Files.write(filePath,data, StandardOpenOption.CREATE_NEW);
            log.info("저장 : {}",filePath.toAbsolutePath());
            return id;
        }catch (FileAlreadyExistsException e){
            throw new RuntimeException("파일이 이미 존재합니다.", e);
        } catch (IOException e){
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        Path filePath=resolvePath(id);
        try{
            return Files.newInputStream(filePath);
        }catch (IOException e){
            throw new RuntimeException("파일을 찾을 수 없습니다.", e);
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
        Path filePath=resolvePath(binaryContentDto.getId());
        File file=filePath.toFile();
        if(!file.exists()){
            return ResponseEntity.notFound().build();
        }
        try{
            InputStreamResource resource = new InputStreamResource(Files.newInputStream(filePath));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(binaryContentDto.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+binaryContentDto.getFileName()+"\"")
                    .body(resource);
        }catch (IOException e){
            throw new RuntimeException("파일 다운로드를 실패하였습니다.", e);
        }
    }


}
