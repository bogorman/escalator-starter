
-- User sessions
CREATE TABLE user_sessions (
    id BIGSERIAL PRIMARY KEY,

    session_id VARCHAR(128) UNIQUE NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,

    email VARCHAR(255) NOT NULL,
    role VARCHAR(50),

    session_type attribute_type
        REFERENCES attributes(attr_type) ON UPDATE CASCADE NOT NULL
        DEFAULT ('USER_SESSION_TYPE', 'UI')
        CHECK ((session_type).attr = 'USER_SESSION_TYPE'),

    session_data JSONB,

    ip_address VARCHAR(45),
    user_agent TEXT,

    last_accessed TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT true,

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);



CREATE INDEX idx_user_sessions_session_id ON user_sessions(session_id);
CREATE INDEX idx_user_sessions_email ON user_sessions(email);
CREATE INDEX idx_user_sessions_user_id ON user_sessions(user_id);
CREATE INDEX idx_user_sessions_expires_at ON user_sessions(expires_at);
CREATE INDEX idx_user_sessions_is_active ON user_sessions(is_active);