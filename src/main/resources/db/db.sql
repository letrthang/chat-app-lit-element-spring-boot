CREATE TABLE users
(
    id           SERIAL PRIMARY KEY,
    user_name    VARCHAR(100) NOT NULL UNIQUE,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    created_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE messages
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
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_parent_message
        FOREIGN KEY (parent_message_id)
            REFERENCES messages (id)
            ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_messages_user_id ON messages (user_id);
CREATE INDEX idx_messages_parent_message_id ON messages (parent_message_id);
CREATE INDEX idx_messages_created_at ON messages (created_at DESC);
CREATE INDEX idx_messages_type ON messages (message_type);

-- Create a view to easily get posts (top-level messages)
CREATE VIEW posts AS
SELECT *
FROM messages
WHERE parent_message_id IS NULL
  AND message_type = 'POST';