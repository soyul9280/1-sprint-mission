package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BinaryContentCreateRequestDto {
    private String fileName;
    private Long size;
    private String contentType;
    private byte[] bytes;
}
