CREATE SEQUENCE app_user_sequence START 1;

CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          first_name VARCHAR(255),
                          last_name VARCHAR(255),
                          email VARCHAR(255) UNIQUE NOT NULL,
                          password VARCHAR(255) NOT NULL,
                          role VARCHAR(255),
                          locked BOOLEAN DEFAULT FALSE,
                          enabled BOOLEAN DEFAULT FALSE
);

CREATE SEQUENCE confirmation_token_sequence START 1;

CREATE TABLE confirmation_token (
                                    id BIGSERIAL PRIMARY KEY,
                                    token VARCHAR(255) NOT NULL,
                                    created_at TIMESTAMP NOT NULL,
                                    expires_at TIMESTAMP NOT NULL,
                                    confirmed_at TIMESTAMP,
                                    app_user_id BIGINT NOT NULL,
                                    CONSTRAINT fk_app_user
                                        FOREIGN KEY (app_user_id)
                                            REFERENCES app_user (id)
                                            ON DELETE CASCADE
);

CREATE SEQUENCE delete_token_sequence START 1;

CREATE TABLE delete_token (
                              id BIGSERIAL PRIMARY KEY,
                              token VARCHAR(255) NOT NULL,
                              created_at TIMESTAMP NOT NULL,
                              expires_at TIMESTAMP NOT NULL,
                              confirmed_at TIMESTAMP,
                              app_user_id BIGINT NOT NULL,
                              CONSTRAINT fk_app_user_delete
                                  FOREIGN KEY (app_user_id)
                                      REFERENCES app_user (id)
                                      ON DELETE CASCADE
);
