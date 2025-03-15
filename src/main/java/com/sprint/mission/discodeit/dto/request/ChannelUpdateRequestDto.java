package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChannelUpdateRequestDto {
    private String newName;

    private String newDescription;
}
