CREATE TABLE IF NOT EXISTS cluster
(
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    cluster_name      VARCHAR(64) NOT NULL,
    bootstrap_servers VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS consumer_topic_offset
(
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    consumer  VARCHAR(64) NOT NULL,
    topic     VARCHAR(64) NOT NULL,
    partition INTEGER     NOT NULL,
    offset    INTEGER     NOT NULL,
    log_size  INTEGER     NOT NULL,
    lag       INTEGER     NOT NULL,
    timestamp INTEGER     NOT NULL
);