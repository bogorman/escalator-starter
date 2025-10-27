-- FOLLOW THE RAILS RULES

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    -- username character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    encrypted_password VARCHAR(128) NOT NULL,
    reset_password_token VARCHAR(255),
    remember_token VARCHAR(255),
    remember_created_at TIMESTAMP,
    confirmation_token VARCHAR(255),
    confirmed_at TIMESTAMP,
    confirmation_sent_at TIMESTAMP,
    password_salt VARCHAR(255),

    -- Profile information
    full_name VARCHAR(255),
    initials VARCHAR(10),
    --bio TEXT,
    --profile_url TEXT,
    --phone VARCHAR(20),
    two_factor_auth_active BOOLEAN DEFAULT false,
    two_factor_auth_secret VARCHAR(255),
    -- Activity tracking
    sign_in_count INTEGER DEFAULT 0,
    current_sign_in_at TIMESTAMP,
    last_sign_in_at TIMESTAMP,
    current_sign_in_ip VARCHAR(255),
    last_sign_in_ip VARCHAR(255),
    -- Status using attributes
    role attribute_type
        REFERENCES attributes(attr_type) ON UPDATE CASCADE NOT NULL
        CHECK ((role).attr = 'USER_ROLE'),

    status attribute_type
        REFERENCES attributes(attr_type) ON UPDATE CASCADE NOT NULL
        CHECK ((status).attr = 'USER_STATUS'),

    access_token UUID NOT NULL DEFAULT gen_random_uuid(),

    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()

);

-- Indices -------------------------------------------------------

CREATE UNIQUE INDEX index_users_on_confirmation_token ON users(confirmation_token text_ops);
CREATE UNIQUE INDEX index_users_on_email ON users(email text_ops);
-- CREATE UNIQUE INDEX index_users_on_username ON users(username text_ops);
CREATE UNIQUE INDEX index_users_on_reset_password_token ON users(reset_password_token text_ops);
CREATE UNIQUE INDEX index_users_on_access_token ON users(access_token);