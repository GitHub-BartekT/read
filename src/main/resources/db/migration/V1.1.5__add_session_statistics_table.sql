CREATE SEQUENCE session_statistics_sequence START 1;

CREATE TABLE session_statistics (
                                    id BIGSERIAL PRIMARY KEY,
                                    user_id BIGINT NOT NULL,
                                    module_id BIGINT NOT NULL,
                                    sentences_count INT NOT NULL,
                                    isFinished BOOLEAN NOT NULL,
                                    ordinal_type VARCHAR(255) DEFAULT 'QUEUE' NOT NULL ,
                                    modulesInSession INT NOT NULL,
                                    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
