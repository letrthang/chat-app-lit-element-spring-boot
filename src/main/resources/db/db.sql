CREATE schema IF NOT EXISTS v2;

CREATE TABLE v2.users
(
    id           SERIAL PRIMARY KEY,
    role         VARCHAR(255),
    user_name    VARCHAR(100) NOT NULL UNIQUE,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    created_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE v2.messages
(
    id                SERIAL PRIMARY KEY,
    user_id           INTEGER      NOT NULL,
    content           VARCHAR(200) NOT NULL,
    parent_message_id INTEGER               DEFAULT NULL,
    message_type      VARCHAR(20)  NOT NULL DEFAULT 'post',
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES v2.users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_parent_message
        FOREIGN KEY (parent_message_id)
            REFERENCES v2.messages (id)
            ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_messages_user_id ON v2.messages (user_id);
CREATE INDEX idx_messages_parent_message_id ON v2.messages (parent_message_id);
CREATE INDEX idx_messages_created_at ON v2.messages (created_at DESC);
CREATE INDEX idx_messages_type ON v2.messages (message_type);

-- Create a view to easily get posts (top-level messages)
CREATE VIEW v2.posts AS
SELECT *
FROM v2.messages
WHERE parent_message_id IS NULL
  AND message_type = 'POST';