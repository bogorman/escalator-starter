CREATE TABLE work_queues (
    id BIGSERIAL PRIMARY KEY,
    
    -- work_type character varying,
    -- work_id bigint,  
    work_type attribute_type
        REFERENCES attributes(attr_type) ON UPDATE CASCADE NOT NULL 
        check ((work_type).attr = 'WORK_TYPE'),    

    work text,
    -- work_status character varying,
    work_status attribute_type
        REFERENCES attributes(attr_type) ON UPDATE CASCADE NOT NULL 
        check ((work_status).attr = 'WORK_STATUS'),   

    work_result text,

    created_at timestamp without time zone NOT NULL DEFAULT NOW(),
    updated_at timestamp without time zone NOT NULL DEFAULT NOW()
);
