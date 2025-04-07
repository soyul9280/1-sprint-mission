package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_ALREADY_EXISTS("이미 존재하는 이메일 혹은 로그인 ID입니다."),
    USER_NOT_FOUND("해당 사용자를 찾을 수 없습니다."),
    USER_STATUS_NOT_FOUND("해당 사용자의 상태정보를 찾을 수 없습니다."),
    USER_STATUS_NOT_FOUND_BY_USERID("해당 사용자의 상태정보를 찾을 수 없습니다."),
    USER_STATUS_ALREADY_EXISTS("해당 사용자의 상태정보가 이미 존재합니다."),


    MESSAGE_NOT_FOUND("해당 메세지를 찾을 수 없습니다."),

    CHANNEL_NOT_FOUND("해당 채널을 찾을 수 없습니다."),
    CHANNEL_UPDATE_FORBIDDEN("Private 채널은 수정할 수 없습니다."),

    READSTATUS_ALREADY_EXISTS("이미 읽음상태가 존재합니다."),
    READSTATUS_NOT_FOUND("해당 읽음상태를 찾을 수 없습니다."),

    FILE_NOT_FOUND("첨부 파일을 찾을 수 없습니다."),
    FILE_NO_RUN("파일 작업 중 오류가 발생하였습니다."),

    LOGIN_USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    LOGIN_PASSWORD_WRONG("비밀번호가 일치하지 않습니다.");

    private final String message;
}
