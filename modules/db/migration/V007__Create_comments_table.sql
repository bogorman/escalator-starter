
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,

    post_id bigint REFERENCES posts(id) NOT NULL,

    active boolean,
    content text,

    created_at timestamp without time zone NOT NULL DEFAULT NOW(),
    updated_at timestamp without time zone NOT NULL DEFAULT NOW()    

);