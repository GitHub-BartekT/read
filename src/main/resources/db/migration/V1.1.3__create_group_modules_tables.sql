CREATE SEQUENCE sessions_sequence START 1;

CREATE TABLE sessions (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         group_name VARCHAR(255) DEFAULT 'new group session' NOT NULL,
                         ordinal_type VARCHAR(255) DEFAULT 'QUEUE' NOT NULL ,
                         ordinal_schema VARCHAR(255),
                         sessions_modules_id BIGINT DEFAULT 1
);

CREATE SEQUENCE sessions_modules_sequence START 1;

CREATE TABLE sessions_modules (
                                      id BIGSERIAL PRIMARY KEY,
                                      sessions_id BIGINT NOT NULL,
                                      module_id BIGINT NOT NULL,
                                      ordinal_position BIGINT DEFAULT 1,
                                      UNIQUE (sessions_id, module_id),
                                      FOREIGN KEY (sessions_id) REFERENCES sessions(id) ON DELETE CASCADE
);