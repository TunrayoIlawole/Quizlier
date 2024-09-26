CREATE SCHEMA IF NOT EXISTS auth_service;

CREATE TYPE user_role AS ENUM ('ADMIN', 'SUPER_ADMIN', 'PLAYER');

CREATE CAST (varchar AS user_role) WITH INOUT AS IMPLICIT;

CREATE TABLE auth_service.user
(
	id bigserial not null,
	firstName varchar(255) not null,
	lastName varchar(255) not null,
	username varchar(255) not null,
	email varchar(255) not null,
	userRole user_role not null,
	password varchar(255) not null,
	highest_score INT default 0,
	created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp,

    CONSTRAINT "PK_USER" PRIMARY KEY ("id")
);