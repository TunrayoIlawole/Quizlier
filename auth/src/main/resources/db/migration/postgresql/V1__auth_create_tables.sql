CREATE TYPE user_role AS ENUM ('admin', 'super-admin', 'player');

CREATE TABLE "user"
(
	id bigserial not null primary key,
	firstName varchar(255) not null,
	lastName varchar(255) not null,
	username varchar(255) not null,
	email varchar(255) not null,
	userRole user_role not null,
	password varchar(255) not null,
	created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
	
);