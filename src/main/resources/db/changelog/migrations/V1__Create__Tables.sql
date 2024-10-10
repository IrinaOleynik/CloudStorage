 -- liquibase formatted sql

 -- changeset Ira:00013
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

 -- changeset Ira:00014
 INSERT INTO public.users (username, password)
 VALUES ('user1@gmail.com','$2a$10$r0doMs/QGOGoiA3IprxJz.a2hwijZ3ut7whxiFFyPnQmI514Z8nYm' );

 -- changeset Ira:00016
 CREATE TABLE files (
     id SERIAL PRIMARY KEY,
     size BIGINT NOT NULL,
     content_type VARCHAR(255) NOT NULL,
     filename VARCHAR(255) NOT NULL,
     owner VARCHAR(255) NOT NULL,
     data OID NOT NULL
 );
