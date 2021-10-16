DROP SCHEMA IF EXISTS project CASCADE;
CREATE SCHEMA project;


CREATE TABLE project.addresses
(
    address_id      SERIAL PRIMARY KEY,
    street          VARCHAR(100) NOT NULL,
    building_number VARCHAR(100) NOT NULL,
    unit_number     VARCHAR(100) NULL,
    postcode        VARCHAR(100) NOT NULL,
    commune         VARCHAR(100) NOT NULL,
    country         VARCHAR(100) NOT NULL
);

CREATE TABLE project.users
(
    user_id            SERIAL PRIMARY KEY,
    username           VARCHAR(100)                                      NOT NULL,
    password           CHARACTER(60)                                     NOT NULL,
    last_name          VARCHAR(100)                                      NOT NULL,
    first_name         VARCHAR(100)                                      NOT NULL,
    address            INTEGER REFERENCES project.addresses (address_id) NOT NULL,
    email              VARCHAR(100)                                      NOT NULL,
    registration_date  TIMESTAMP                                         NOT NULL,
    valid_registration BOOLEAN                                           NOT NULL,
    user_type          INTEGER
);

CREATE TABLE project.visit_requests
(
    visit_request_id    SERIAL PRIMARY KEY,
    request_date        TIMESTAMP                                  NOT NULL,
    time_slot           TEXT                                       NOT NULL,
    address             INTEGER REFERENCES project.addresses (address_id) NULL,
    status              INTEGER                                    NOT NULL,
    chosen_date_time    TIMESTAMP NULL,
    cancellation_reason TEXT NULL,
    customer            INTEGER REFERENCES project.users (user_id) NOT NULL
);

CREATE TABLE project.furniture_types
(
    type_id SERIAL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL
);

CREATE TABLE project.furniture
(
    furniture_id                  SERIAL PRIMARY KEY,
    description                   TEXT                                                         NOT NULL,
    type                          INTEGER REFERENCES project.furniture_types (type_id)         NOT NULL,
    visit_request                 INTEGER REFERENCES project.visit_requests (visit_request_id) NOT NULL,
    purchase_price                DECIMAL NULL,
    withdrawal_date_from_customer TIMESTAMP NULL,
    selling_price                 DECIMAL NULL,
    special_sale_price            DECIMAL NULL,
    deposit_date                  TIMESTAMP NULL,
    selling_date                  TIMESTAMP NULL,
    delivery_date                 TIMESTAMP NULL,
    withdrawal_date_to_customer   TIMESTAMP NULL,
    buyer                         INTEGER REFERENCES project.users (user_id) NULL,
    condition                     INTEGER NULL,
    unregistered_buyer_email      VARCHAR(100) NULL
    --favourite_picture INTEGER REFERENCES project.pictures (picture_id) NULL
);


CREATE TABLE project.pictures
(
    picture_id           SERIAL PRIMARY KEY,
    name                 VARCHAR(100)                                        NOT NULL,
    visible_for_everyone BOOLEAN                                             NOT NULL,
    furniture            INTEGER REFERENCES project.furniture (furniture_id) NOT NULL,
    scrolling_picture    BOOLEAN                                             NOT NULL
);

ALTER TABLE project.furniture
    ADD favourite_picture INTEGER NULL;
ALTER TABLE project.furniture
    ADD FOREIGN KEY (favourite_picture) REFERENCES project.pictures (picture_id);

CREATE TABLE project.options
(
    option_id SERIAL PRIMARY KEY,
    buyer     INTEGER REFERENCES project.users (user_id)          NOT NULL,
    furniture INTEGER REFERENCES project.furniture (furniture_id) NOT NULL,
    duration  INTEGER                                             NOT NULL,
    date      TIMESTAMP                                           NOT NULL,
    status    INTEGER                                             NOT NULL
);

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Armoire');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Bahut');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Bibliothèque');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Bonnetière');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Buffet');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Bureau');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Chaise');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Chiffonier');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Coffre');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Coiffeuse');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Commode');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Confident/Indiscret');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Console');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Dresse');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Fauteuil');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Guéridon');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Lingère');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Lit');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Penderie');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Secrétaire');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Table');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Tabouret');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Vaisselier');

INSERT INTO project.furniture_types
VALUES (DEFAULT, 'Valet muet');

--Scénario test livrable 5
--Inserts admins
INSERT INTO project.addresses
VALUES (DEFAULT, 'sente des artistes', '1bis', DEFAULT, '4800', 'Verviers', 'Belgique');

INSERT INTO project.users
VALUES (DEFAULT, 'bert', '$2a$10$YnWAR46MkSLTOBR.ptW63uNwsFYSO5aGQ8I/N..Z4mZokFwNShGs.', 'Satcho',
        'Albert', 1, 'bert.satcho@gmail.be', '2021-03-22', true, 2);

INSERT INTO project.addresses
VALUES (DEFAULT, 'sente des artistes', '18', DEFAULT, '4800', 'Verviers', 'Belgique');

INSERT INTO project.users
VALUES (DEFAULT, 'lau', '$2a$10$BSS3FijyW9M2TZ1brbdSP.nB8Mo6Y/Dx28oHGEdP4MB2SjakYTZvW', 'Satcho',
        'Laurent', 2, 'laurent.satcho@gmail.be', '2021-03-22', true, 2);

UPDATE project.users
SET registration_date = '2021-03-22'
WHERE user_id = 2;

UPDATE project.users
SET valid_registration = true
WHERE user_id = 1;

UPDATE project.users
SET user_type = 2
WHERE user_id = 2;

--Inserts clients
INSERT INTO project.addresses
VALUES (DEFAULT, 'Rue de l Eglise', '11', 'B1', '4987', 'Stoumont', 'Belgique');

INSERT INTO project.users
VALUES (DEFAULT, 'Caro', '$2a$10$Olb5dluFvgTVFW7MhGe6jODO8C6edw9qhkow0X0pYleOYB7HsYQR.', 'Line',
        'Caroline', 3, 'caro.line@hotmail.com', '2021-03-23', true, 1);

INSERT INTO project.addresses
VALUES (DEFAULT, 'Rue de Renkin', '7', DEFAULT, '4800', 'Verviers', 'Belgique');

INSERT INTO project.users
VALUES (DEFAULT, 'achil', '$2a$10$UKx1LZF5Np1JjgM5mDfsQeth.sNVcKAFHIJtaQA0DvISmNeQz7qeW', 'Ile',
        'Achille', 4, 'ach.ile@gmail.com', '2021-03-23', true, 0);

INSERT INTO project.addresses
VALUES (DEFAULT, 'Lammerskreuzstrasse', '6', DEFAULT, '52159', 'Roetgen', 'Allemagne');

INSERT INTO project.users
VALUES (DEFAULT, 'bazz', '$2a$10$dwQcHOd5M8OBj6ckqlsfHu6KlKrLVEQzxhyF99iNo9G7F6NSA9C2G', 'Ile',
        'Basile', 5, 'bas.ile@gmail.be', '2021-03-23', true, 0);

INSERT INTO project.users
VALUES (DEFAULT, 'Theo', '$2a$10$xQTLazOt4NzYaa8xWDmzbOFqM3i.skzWSW82qjgCYp3W.JMoFKZvO', 'Ile',
        'Théophile', 4, 'theo.phile@proximus.be', '2021-03-30', true, 1);

INSERT INTO project.addresses
VALUES (DEFAULT, 'Rue des Minières','45','Ter','4800','Verviers','Belgique');

INSERT INTO project.users
VALUES (DEFAULT, 'charline', '$2a$10$fYinLOg.nzo9Grui7O2yTeQevihMa5wnjq3o7Jk1x59KnqzLTfM8i', 
        'Line','Charles', 6, 'charline@proximus.be', '2021-04-22', true, 0);

UPDATE project.users
SET registration_date = '2021-03-23'
WHERE user_id = 5;

--Insert demandes de visite
INSERT INTO project.visit_requests
VALUES (DEFAULT, '2021-03-24', 'lundi de 18h à 22h', 4, 2, '2021-03-29 20:00:00', DEFAULT, 4);

INSERT INTO project.visit_requests
VALUES (DEFAULT, '2021-03-25', 'lundi de 18h à 22h', 4, 0, DEFAULT, 'Meuble trop récent', 4);

INSERT INTO project.visit_requests
VALUES (DEFAULT, '2021-03-25', 'tous les jours de 15h à 18h', 5, 2, '2021-03-29 15:00:00', DEFAULT,
        5);

INSERT INTO project.addresses
VALUES (DEFAULT, 'Rue Victor Bouillenne','9','4C','4800','Verviers','Belgique');

INSERT INTO project.visit_requests
VALUES (DEFAULT,'2021-04-21', 'tous les matins de 9h à 13h', 7, 1, null,DEFAULT,6);

INSERT INTO project.visit_requests
VALUES (DEFAULT,'2021-04-22', 'tous les jours de 16h à 19h', 6, 2, '2021-04-26 18:00:00', 
        DEFAULT, 6);

--Inserts meubles
INSERT INTO project.furniture
VALUES (DEFAULT, 'Bahut profond d’une largeur de 112 cm et d’une hauteur de 147cm.', 2, 1, 200,
        '2021-03-30', DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, 5, DEFAULT, DEFAULT);

INSERT INTO project.furniture
VALUES (DEFAULT, 'Large bureau 1m87 cm, deux colonnes de tiroirs', 6, 1, 159, '2021-03-30', 299,
        DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, 6, DEFAULT, DEFAULT);

INSERT INTO project.furniture
VALUES (DEFAULT, 'Table jardin en bois brut', 21, 2, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT,
        DEFAULT, DEFAULT, DEFAULT, DEFAULT, 1, DEFAULT, DEFAULT);

INSERT INTO project.furniture
VALUES (DEFAULT, 'Table en chêne, pieds en fer forgé', 21, 3, 140, '2021-03-29', 459, DEFAULT,
        DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, 7, DEFAULT, DEFAULT);

INSERT INTO project.furniture
VALUES (DEFAULT, 'Secrétaire en acajou, marqueterie', 20, 3, 90, '2021-03-29', DEFAULT, DEFAULT,
        DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, 5, DEFAULT, DEFAULT);

INSERT INTO project.furniture
VALUES (DEFAULT, 'Lit à baldaquin en acajou', 18, 4, DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT,
        DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, 0, DEFAULT, DEFAULT);

INSERT INTO project.furniture
VALUES (DEFAULT, 'Bureau en bois ciré', 6, 5, 220, '2021-04-27', DEFAULT, DEFAULT, DEFAULT,
        DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, 4, DEFAULT, DEFAULT);

INSERT INTO project.furniture
VALUES (DEFAULT, 'Bureau en chêne massif, sous-main intégré', 6, 5, 325, '2021-04-27', 378, DEFAULT, DEFAULT,
        DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, 6, DEFAULT, DEFAULT);

INSERT INTO project.furniture
VALUES (DEFAULT, 'Magnifique bureau en acajou', 6, 5, 180, '2021-04-27', 239, DEFAULT, DEFAULT,
        DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, 6, DEFAULT, DEFAULT);

INSERT INTO project.furniture
VALUES (DEFAULT, 'Splendide coiffeuse aux reliefs travaillés', 10, 5, 150, '2021-04-27', 199, DEFAULT, DEFAULT,
        DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, 6, DEFAULT, DEFAULT);

INSERT INTO project.furniture
VALUES (DEFAULT, 'Coiffeuse marqueterie', 10, 5, 145, '2021-04-27', 199, DEFAULT, DEFAULT,
        DEFAULT, DEFAULT, DEFAULT, DEFAULT, DEFAULT, 6, DEFAULT, DEFAULT);

--Inserts pictures
INSERT INTO project.pictures
VALUES (DEFAULT, 'Bahut_2.png', false, 1, false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'Bureau_1.png', false, 2, false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'Bureau_1-Visible-Préférée.png', true, 2,false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'table-jardin-recente.jpg', false, 3, false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'Table.jpg', false, 4, false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'Secretaire.png', false, 5, false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'Lit_LitBaldaquin.jpg', false, 6, false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'Bureau_2.jpg', false, 7, false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'Bureau-3_ImageClient.jpg', false, 8, false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'Bureau-3-Visible.jpg', true, 8, false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'Bureau-3-Visible-Préférée.jpg', true, 8, false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'Bureau-8.jpg', false, 9, false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'Bureau-8-Visible-Préférée.jpg', true, 9, false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'Coiffeuse_1_Details.jpg', false, 10, false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'Coiffeuse_1-Visible_Préférée.jpg', true, 10, false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'Coiffeuse_2.png', false, 11, false);

INSERT INTO project.pictures
VALUES (DEFAULT, 'Coiffeuse_2-Visible_Préférée.png', true, 11, false);

UPDATE project.furniture SET favourite_picture = 3 WHERE furniture_id = 2;
UPDATE project.furniture SET favourite_picture = 11 WHERE furniture_id = 8;
UPDATE project.furniture SET favourite_picture = 13 WHERE furniture_id = 9;
UPDATE project.furniture SET favourite_picture = 15 WHERE furniture_id = 10;
UPDATE project.furniture SET favourite_picture = 17 WHERE furniture_id = 11; 

SELECT COUNT(*) FROM project.pictures p WHERE p.visible_for_everyone = true;
SELECT COUNT(*) FROM project.furniture f WHERE f.condition = 6 OR f.condition = 8 OR f.condition = 9;
SELECT COUNT(*) FROM project.furniture f WHERE f.favourite_picture IS NOT NULL;
