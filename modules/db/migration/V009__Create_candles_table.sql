CREATE TABLE candles (
    id BIGSERIAL PRIMARY KEY,    
    --
    token_address character varying NOT NULL REFERENCES tokens(address),
    -- exchange character varying NOT NULL REFERENCES exchanges (ident) ON UPDATE CASCADE,
    --
    -- base character varying NOT NULL REFERENCES assets (ident),
    -- quote character varying NOT NULL REFERENCES assets (ident),
    -- instrument_symbol character varying NOT NULL REFERENCES instruments (symbol) ON UPDATE CASCADE,
    --
    open_time bigint NOT NULL,
    close_time bigint NOT NULL,
    open_timestamp timestamp without time zone NOT NULL,
    close_timestamp timestamp without time zone NOT NULL,
    duration bigint NOT NULL,
    open DOUBLE PRECISION NOT NULL,
    high DOUBLE PRECISION NOT NULL,
    low DOUBLE PRECISION NOT NULL,
    close DOUBLE PRECISION NOT NULL,
    volume DOUBLE PRECISION NOT NULL,
    created_at timestamp without time zone NOT NULL DEFAULT NOW(),
    updated_at timestamp without time zone NOT NULL DEFAULT NOW()
    --,
    -- FOREIGN KEY (exchange, instrument_symbol) REFERENCES instruments (exchange,symbol)
);

-- CREATE UNIQUE INDEX index_candles_on_entry ON candles (exchange,instrument_symbol,open_time,duration);
-- CREATE UNIQUE INDEX index_candles_on_entry2 ON candles (exchange,instrument_symbol,close_time,duration);