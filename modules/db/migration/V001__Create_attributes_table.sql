
create type attribute_type as (
   attr character varying(255),
   ident character varying(255)
);

CREATE FUNCTION attribute_type_to_string(attribute_type)
RETURNS varchar AS $$
BEGIN
    RETURN ($1.attr || ' ' || $1.ident);
END;
$$ LANGUAGE plpgsql IMMUTABLE;

CREATE TABLE attributes (
    id BIGSERIAL PRIMARY KEY,
    attr_type attribute_type NOT NULL,
    ordering integer,
    description character varying(255) NOT NULL,
    created_at timestamp without time zone NOT NULL DEFAULT NOW(),
    updated_at timestamp without time zone NOT NULL DEFAULT NOW()    
);

create unique index attributes_uidx on attributes(attr_type);

-----------------------

-- create type correlation_type as (
--    id character varying(255),
-- );

-----FOR ES

-- CREATE DOMAIN correlation_id_type AS CHAR(26);

-- ----------------------------------

-- -- Table Definition ----------------------------------------------

-- CREATE TABLE persistence_offset (
--     id BIGSERIAL PRIMARY KEY,
--     persistence_class character varying,
--     "offset" bigint NOT NULL,
--     created_at timestamp without time zone NOT NULL DEFAULT NOW(),
--     updated_at timestamp without time zone NOT NULL DEFAULT NOW()        
-- );

-- -- Indices -------------------------------------------------------

-- CREATE UNIQUE INDEX persistence_offset_class_ ON persistence_offset(persistence_class);
