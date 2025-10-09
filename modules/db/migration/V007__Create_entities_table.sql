-- Table Definition ----------------------------------------------

CREATE TABLE entities (
    id BIGSERIAL PRIMARY KEY,
    keywords character varying[] DEFAULT '{}'::character varying[],

    entity_type attribute_type
        REFERENCES attributes(attr_type) ON UPDATE CASCADE NOT NULL 
        check ((entity_type).attr = 'ENTITY_TYPE'),   

    entity_object_id bigint,
    user_id bigint REFERENCES users(id) NOT NULL,

    url character varying,
    content text,
    domains character varying[] DEFAULT '{}'::character varying[],
    tag character varying,

    original_image character varying,
    cropped_image character varying,
    mini_image character varying,
    cropped_cover_image character varying,

    blacklisted boolean,
    -- rank_to_delete integer,
    weighting double precision,
    parent_entity_id bigint REFERENCES entities (id),
    wdu character varying,
    external_entity_id character varying,

    precreated boolean,
    verified boolean,

    claimed boolean,
    claimed_at timestamp without time zone,
    
    -- external_last_changed_at timestamp without time zone,
    active boolean DEFAULT true,

    settings  jsonb NOT NULL default '{}'::jsonb,
    personal  jsonb NOT NULL default '{}'::jsonb,
    professional  jsonb NOT NULL default '{}'::jsonb,    

    -- handle_id bigint,
    -- handle character varying,    
    -- social_entities 
    -- user_id bigint REFERENCES users(id),

    subline character varying,

    country_code character varying,
    hash_tags character varying[] DEFAULT '{}'::character varying[],
    search_keywords character varying[] DEFAULT '{}'::character varying[],
    
    original_cover_image character varying,
    created_by bigint,
    validation_keywords character varying[] DEFAULT '{}'::character varying[],
    news_topics character varying[] DEFAULT '{}'::character varying[],
    news_verification_required boolean,
    rejection_keywords character varying[] DEFAULT '{}'::character varying[],

    -- kg_id character varying,
    -- wikipedia_url character varying,
    -- wd_id character varying,
    -- data_verified_at timestamp without time zone,    

    -- entity_meta_id bigint, --NOT NULL REFERENCES entity_metas(id),


    ref_entity_type attribute_type
        REFERENCES attributes(attr_type) ON UPDATE CASCADE
        check ((entity_type).attr = 'ENTITY_TYPE'),   

    created_at timestamp without time zone NOT NULL DEFAULT NOW(),
    updated_at timestamp without time zone NOT NULL DEFAULT NOW()
);

-- Indices -------------------------------------------------------

-- CREATE UNIQUE INDEX entities_pkey ON entities(id int8_ops);
CREATE UNIQUE INDEX tag_uniq_idx ON entities(tag);
