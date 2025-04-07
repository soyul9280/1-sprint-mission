package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BinaryContentCreateRequestDto {

    @NotBlank(message = "파일 이름은 필수입니다.")
    private String fileName;

    @NotNull(message = "파일 크기는 필수입니다.")
    @Positive(message = "파일 크기는 양수여야합니다.")
    private Long size;

    @NotBlank(message = "콘텐츠 타입은 필수입니다.")
    private String contentType;

    @NotNull(message = "파일 데이터는 필수입니다.")
    private byte[] bytes;
}
