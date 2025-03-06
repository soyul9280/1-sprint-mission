CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL ,
    password VARCHAR(60) NOT NULL ,
    profile_id UUID REFERENCES binary_contents(id) ON DELETE SET NULL
);

CREATE TABLE binary_contents(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    file_name VARCHAR(255) NOT NULL ,
    size BIGINT NOT NULL ,
    content_type VARCHAR(100) NOT NULL ,
    bytes BYTEA NOT NULL
);

CREATE TABLE channels(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL ,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL ,
    name VARCHAR(100),
    description VARCHAR(500),
    type VARCHAR(10) NOT NULL CHECK ( type IN ('PUBLIC','PRIVATE'))
);

CREATE TABLE messages(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL ,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    content TEXT,
    channel_id UUID NOT NULL REFERENCES channels(id) ON DELETE CASCADE ,
    author_id UUID REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE read_statuses(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL ,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE ,
    channel_id UUID REFERENCES channels(id) ON DELETE CASCADE ,
    last_read_at TIMESTAMPTZ NOT NULL,
    UNIQUE (user_id,channel_id)
);

CREATE TABLE user_statuses(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL ,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE ,
    last_active_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE message_attachments(
    user_id UUID REFERENCES users(id) ON DELETE CASCADE ,
    attachment_id UUID REFERENCES binary_contents(id) ON DELETE CASCADE
);





