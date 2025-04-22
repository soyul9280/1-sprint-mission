package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.file.FileNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    @Transactional
    public BinaryContentDto create(BinaryContentCreateRequestDto request) {
        log.debug("바이너리 컨텐츠 생성 시작: filename={},size={},contentType={}",request.getFileName(),request.getSize(),request.getContentType());
        String fileName = request.getFileName();
        byte[] bytes = request.getBytes();
        Long size = request.getSize();
        String contentType = request.getContentType();
        BinaryContent content = new BinaryContent(fileName, size, contentType);
        BinaryContent savedContent = binaryContentRepository.save(content);
        binaryContentStorage.put(savedContent.getId(), bytes);
        log.info("바이너리 컨텐츠 생성 완료: id={},fileName={},size={}",savedContent.getId(),fileName,size);
        return binaryContentMapper.toDto(savedContent);
    }

    @Override
    public BinaryContentDto find(UUID binaryContentId) {
        log.debug("바이너리 컨텐츠 조회 시작: id={}",binaryContentId);
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId).orElseThrow(() -> new FileNotFoundException(binaryContentId));
        log.info("바이너리 컨텐츠 조회 완료: id={},fileName={}",binaryContentId,binaryContent.getFileName());
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        log.debug("바이너리 컨텐츠 목록 조회 시작: ids={}",binaryContentIds);
        List<BinaryContentDto> list = binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
                .map(binaryContentMapper::toDto)
                .toList();
        log.info("바이너리 컨텐츠 목록 조회 완료: 조회된 항목 수={}", list.size());
        return list;
    }

    @Override
    @Transactional
    public void delete(UUID binaryContentId) {
        log.debug("바이너리 컨텐츠 삭제 시작: id={}",binaryContentId);
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId).orElseThrow(() -> new FileNotFoundException(binaryContentId));
        binaryContentRepository.deleteById(binaryContent.getId());
        log.info("바이너리 컨텐츠 삭제 완료: id={}",binaryContentId);
    }
}
