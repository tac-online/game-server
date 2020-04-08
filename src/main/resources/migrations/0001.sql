--liquibase formatted sql

--changeset jwirth:1
CREATE TABLE IF NOT EXISTS games (id SERIAL , player_id BIGINT UNSIGNED NOT NULL);
--rollback drop table games;