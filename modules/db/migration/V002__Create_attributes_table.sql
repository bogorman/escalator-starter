
create type attribute_type as (
   attr character varying(255),
   ident character varying(255)
);

CREATE TABLE attributes (
    id BIGSERIAL PRIMARY KEY,
    attr_type attribute_type NOT NULL,
    ordering integer,
    description character varying(255) NOT NULL,
    created_at timestamp without time zone NOT NULL DEFAULT NOW(),
    updated_at timestamp without time zone NOT NULL DEFAULT NOW()    
);

create unique index attributes_uidx on attributes(attr_type);
