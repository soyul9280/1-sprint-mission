package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PrivateChannelCreateRequestDto {

    @NotNull(message = "참여 유저 목록은 필수입니다.")
    @Size(min = 1,message = "최소 한명이상의 유저가 필요합니다.")
    private List<UUID> participantIds;
}
