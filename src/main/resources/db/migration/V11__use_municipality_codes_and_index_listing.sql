UPDATE app_publications.publications AS p
SET municipality = m.code
FROM app_shared.municipalities AS m
WHERE LOWER(m.name) = LOWER(BTRIM(p.municipality));

UPDATE app_users.users AS u
SET municipality = m.code
FROM app_shared.municipalities AS m
WHERE LOWER(m.name) = LOWER(BTRIM(u.municipality));

ALTER TABLE app_publications.publications
    ALTER COLUMN municipality TYPE VARCHAR(5),
    ADD CONSTRAINT chk_publications_municipality_code
        CHECK (municipality ~ '^[0-9]{5}$');

ALTER TABLE app_users.users
    ALTER COLUMN municipality TYPE VARCHAR(5),
    ADD CONSTRAINT chk_users_municipality_code
        CHECK (municipality ~ '^[0-9]{5}$');

CREATE INDEX idx_publications_listing_by_municipality
    ON app_publications.publications (status, municipality, created_at DESC, id DESC);

CREATE INDEX idx_publications_listing
    ON app_publications.publications (status, created_at DESC, id DESC);
