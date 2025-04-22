package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class FileRunException extends FileException {

    public FileRunException(UUID fileId) {
        super(ErrorCode.FILE_NO_RUN, Map.of("fileId", fileId));
    }
}
