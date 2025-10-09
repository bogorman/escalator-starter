CREATE TABLE tokens (
    id BIGSERIAL PRIMARY KEY,
    -- instrument_class character varying REFERENCES instrument_classes (instruemnt_class) NOT NULL,
    -- platform_type character varying,-- REFERENCES platforms (ident),
    -- platform_ident character varying,-- REFERENCES platforms (ident),

    chain_ident character varying, -- REFERENCES chains (ident),
    --
    --
    address character varying NOT NULL, 
    --
    symbol character varying NOT NULL,
    name character varying NOT NULL,
    --
    total_supply double precision,
    max_supply double precision,
    icon character varying,
    color character varying,
    active boolean NOT NULL DEFAULT true,
    disabled_at timestamp without time zone,
    tags character varying[] DEFAULT '{}'::character varying[], 

    -- platform_ident character varying REFERENCES platforms (ident),

    -- "platform": {
    --         "id": 1027,
    --         "name": "Ethereum",
    --         "symbol": "ETH",
    --         "slug": "ethereum",
    --         "token_address": "0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48"
    --     }

    created_at timestamp without time zone NOT NULL DEFAULT NOW(),
    updated_at timestamp without time zone NOT NULL DEFAULT NOW()
    -- FOREIGN KEY (platform_type, platform_ident) REFERENCES platforms (platform_type, ident)
);


-- this should be ignored when the code is generated.
CREATE UNIQUE INDEX index_tokens_on_address ON tokens(address);

-- CREATE UNIQUE INDEX index_crypto_currencies_on_symbol ON crypto_currencies(symbol);