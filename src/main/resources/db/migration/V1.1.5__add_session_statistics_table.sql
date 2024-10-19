CREATE SEQUENCE session_statistics_sequence START 1;

CREATE TABLE session_statistics (
                                    id BIGSERIAL PRIMARY KEY,
                                    user_id BIGINT NOT NULL,
                                    session_id BIGINT NOT NULL,
                                    sentences_count INT NOT NULL,
                                    is_finished BOOLEAN NOT NULL,
                                    ordinal_type VARCHAR(255) DEFAULT 'QUEUE' NOT NULL ,
                                    modules_in_session INT NOT NULL,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
