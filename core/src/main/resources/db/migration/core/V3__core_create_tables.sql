CREATE TABLE IF NOT EXISTS core_service.category
(
	id bigserial not null primary key,
	name varchar(255) not null,
	description text not null,
	createdAt timestamp default current_timestamp,
    updatedAt timestamp default current_timestamp,
    deletedAt timestamp default current_timestamp
	
);

CREATE TABLE IF NOT EXISTS core_service.question
(
	id bigserial not null primary key,
	question varchar(255) not null,
	createdAt timestamp default current_timestamp,
    updatedAt timestamp default current_timestamp,
    deletedAt timestamp default current_timestamp,
    categoryId bigserial,
    CONSTRAINT fk_categoryId FOREIGN KEY (categoryId) REFERENCES core_service.category (id)
	
);

CREATE TABLE IF NOT EXISTS core_service.option
(
	id bigserial not null primary key,
	optionText text not null,
	isCorrect boolean not null,
	createdAt timestamp default current_timestamp,
    updatedAt timestamp default current_timestamp,
    deletedAt timestamp default current_timestamp,
    questionId bigint not null,
    CONSTRAINT fk_questionId FOREIGN KEY (questionId) REFERENCES core_service.question (id)
	
);

