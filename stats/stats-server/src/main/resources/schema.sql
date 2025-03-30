DROP TABLE if EXISTS hits CASCADE;

CREATE TABLE IF NOT EXISTS hits(
    id SERIAL PRIMARY KEY,
    app varchar(255) NOT NULL,
    uri varchar(255) NOT NULL,
    ip varchar(255) NOT NULL,
    timestamp date NOT NULL
);