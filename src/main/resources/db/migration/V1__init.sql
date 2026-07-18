CREATE TABLE roles (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    id             BIGSERIAL PRIMARY KEY,
    username       VARCHAR(20)  NOT NULL UNIQUE,
    email          VARCHAR(254) NOT NULL UNIQUE,
    password_hash  VARCHAR(60)  NOT NULL,
    creation_time  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    update_time    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    email_verified BOOLEAN      NOT NULL DEFAULT FALSE,
    blocked        BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_users_creation_time ON users (creation_time);

CREATE TABLE users_roles (
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE OR REPLACE FUNCTION set_users_update_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_update_time
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION set_users_update_time();

INSERT INTO roles (name) VALUES ('ROLE_USER');
