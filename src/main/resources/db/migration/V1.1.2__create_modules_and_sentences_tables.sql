CREATE SEQUENCE modules_sequence START 1;

CREATE TABLE modules (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         module_name VARCHAR(255) NOT NULL,
                         sessions_per_day INT DEFAULT 1,
                         presentations_per_session INT DEFAULT 1,
                         new_sentences_per_day INT DEFAULT 1,
                         actual_day INT DEFAULT 1,
                         next_session INT DEFAULT 1,
                         is_private BOOLEAN DEFAULT TRUE
);


CREATE SEQUENCE sentences_sequence START 1;

CREATE TABLE sentences (
                           id BIGSERIAL PRIMARY KEY,
                           module_id BIGINT NOT NULL,
                           user_id BIGINT NOT NULL,
                           ordinal_number BIGINT,
                           sentence TEXT NOT NULL,
                           CONSTRAINT fk_module
                               FOREIGN KEY (module_id)
                                   REFERENCES modules (id)
                                   ON DELETE CASCADE
);