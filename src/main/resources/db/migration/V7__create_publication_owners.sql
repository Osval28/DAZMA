CREATE TABLE app_publications.publication_owners (
    user_id     UUID PRIMARY KEY,
    external_id VARCHAR(255) NOT NULL UNIQUE,
    state       VARCHAR(20)  NOT NULL,

    CONSTRAINT chk_publication_owners_state
        CHECK (state IN ('ACTIVE', 'SUSPENDED'))
);
