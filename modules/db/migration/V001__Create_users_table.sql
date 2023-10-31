-- FOLLOW THE RAILS RULES

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    encrypted_password character varying(128) NOT NULL,
    reset_password_token character varying(255),
    remember_token character varying(255),
    remember_created_at timestamp without time zone,
    sign_in_count integer NOT NULL DEFAULT 0,
    current_sign_in_at timestamp without time zone,
    last_sign_in_at timestamp without time zone,
    current_sign_in_ip character varying(255),
    last_sign_in_ip character varying(255),
    confirmation_token character varying(255),
    confirmation_at timestamp without time zone,
    confirmation_sent_at timestamp without time zone,
    password_salt character varying(255),
    full_name character varying(255) NOT NULL ,
    initials character varying(255) NOT NULL ,
    two_factor_auth_active boolean DEFAULT false,
    two_factor_auth_secret character varying,
    role character varying,
    status character varying(255) NOT NULL ,
    created_at timestamp without time zone NOT NULL DEFAULT NOW(),
    updated_at timestamp without time zone NOT NULL DEFAULT NOW()

);

-- Indices -------------------------------------------------------

CREATE UNIQUE INDEX index_users_on_confirmation_token ON users(confirmation_token text_ops);
CREATE UNIQUE INDEX index_users_on_email ON users(email text_ops);
CREATE UNIQUE INDEX index_users_on_username ON users(username text_ops);
CREATE UNIQUE INDEX index_users_on_reset_password_token ON users(reset_password_token text_ops);