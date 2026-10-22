ALTER TABLE app_publications.publications
    RENAME COLUMN city TO municipality;

ALTER TABLE app_publications.publications
    RENAME CONSTRAINT chk_publications_city TO chk_publications_municipality;
