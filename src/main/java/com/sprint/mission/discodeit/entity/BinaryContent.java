package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(of = {"fileName","size","contentType"})
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity {

    private String fileName;
    private Long size;
    private String contentType;

    public BinaryContent(String fileName, Long size, String contentType) {
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
    }
}
