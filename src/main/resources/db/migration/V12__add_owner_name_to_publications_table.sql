ALTER TABLE app_publications.publication_owners
ADD user_name varchar(255);

ALTER TABLE app_publications.publications
ADD owner_name varchar(255);