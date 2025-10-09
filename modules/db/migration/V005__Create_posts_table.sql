
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,

    user_id bigint REFERENCES users(id) NOT NULL,
    active boolean,
    content text,

    created_at timestamp without time zone NOT NULL DEFAULT NOW(),
    updated_at timestamp without time zone NOT NULL DEFAULT NOW()    

);