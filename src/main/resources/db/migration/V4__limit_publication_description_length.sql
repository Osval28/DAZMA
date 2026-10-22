ALTER TABLE app_publications.publications
    ADD CONSTRAINT chk_publications_description_length
        CHECK (char_length(description) <= 1000);