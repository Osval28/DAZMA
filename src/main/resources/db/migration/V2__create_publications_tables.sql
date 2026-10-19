CREATE SCHEMA IF NOT EXISTS app_publications;

CREATE TABLE app_publications.publications (
    id          UUID PRIMARY KEY,
    owner_id    UUID         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    type        VARCHAR(20)  NOT NULL,
    description TEXT         NOT NULL,
    city        VARCHAR(255) NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    price       INTEGER      NOT NULL,

    CONSTRAINT chk_publications_price
        CHECK (price > 0),
    CONSTRAINT chk_publications_name
        CHECK (BTRIM(name) <> ''),
    CONSTRAINT chk_publications_type
        CHECK (type IN ('PURCHASE', 'EXCHANGE')),
    CONSTRAINT chk_publications_description
        CHECK (BTRIM(description) <> ''),
    CONSTRAINT chk_publications_city
        CHECK (BTRIM(city) <> ''),
    CONSTRAINT chk_publications_status
        CHECK (status IN ('PUBLISHED'))
);

CREATE TABLE app_publications.publication_photos (
    id             UUID PRIMARY KEY,
    publication_id UUID          NOT NULL,
    object_key     VARCHAR(1024) NOT NULL UNIQUE,
    display_order  INTEGER       NOT NULL,

    CONSTRAINT fk_publication_photos_publication
        FOREIGN KEY (publication_id)
        REFERENCES app_publications.publications(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_publication_photos_object_key
        CHECK (BTRIM(object_key) <> ''),
    CONSTRAINT chk_publication_photos_display_order
        CHECK (display_order >= 0),
    CONSTRAINT uq_publication_photos_order
        UNIQUE (publication_id, display_order)
);

CREATE INDEX idx_publications_owner_id
    ON app_publications.publications(owner_id);

CREATE INDEX idx_publications_created_at
    ON app_publications.publications(created_at);
