package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity{
        private String name;
        private String description;

        public Channel(String name, String description) {
            super();
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public void updateName(String name) {
            this.name = name;
            updateUpdatedAt();
        }

        public void updateDescription(String description) {
            this.description = description;
            updateUpdatedAt();
        }
    }
