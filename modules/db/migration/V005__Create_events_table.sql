-- --test for uuid as ID

-- CREATE TABLE events (
--     -- id BIGSERIAL PRIMARY KEY,
--     id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
--     event_type character varying(255),
--     event_name character varying(255),
--     created_at timestamp without time zone NOT NULL DEFAULT NOW(),
--     updated_at timestamp without time zone NOT NULL DEFAULT NOW()    
-- );

-- -- CREATE UNIQUE INDEX index_users_on_reset_password_token ON users(reset_password_token text_ops);