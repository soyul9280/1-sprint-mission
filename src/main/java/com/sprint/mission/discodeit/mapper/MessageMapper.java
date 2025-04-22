package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageMapper {
   private final UserMapper userMapper;
   private final BinaryContentMapper binaryContentMapper;

   public MessageDto toDto(Message message) {
       if(message == null) {
           return null;
       }
       List<BinaryContent> attachments = message.getAttachments();
       List<BinaryContentDto> binaryContentDtos = new ArrayList<>();
       for (BinaryContent attachment : attachments) {
           binaryContentDtos.add(binaryContentMapper.toDto(attachment));
       }
       return new MessageDto(
               message.getId(),
               message.getCreatedAt(),
               message.getUpdatedAt(),
               message.getContent(),
               message.getChannel().getId(),
               userMapper.toDto(message.getAuthor()),
               binaryContentDtos
       );
   }
}
