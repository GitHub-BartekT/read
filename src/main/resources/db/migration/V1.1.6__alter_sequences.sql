ALTER SEQUENCE sentences_sequence INCREMENT BY 20;
DROP SEQUENCE IF EXISTS app_user_sequence;
DROP SEQUENCE IF EXISTS confirmation_token_sequence;
DROP SEQUENCE IF EXISTS delete_token_sequence;

DROP SEQUENCE IF EXISTS modules_sequence;
DROP SEQUENCE IF EXISTS sessions_sequence;
DROP SEQUENCE IF EXISTS sessions_modules_sequence;
DROP SEQUENCE IF EXISTS session_statistics_sequence;