CREATE TABLE "categories"
(
	id bigserial not null primary key,
	name varchar(255) not null,
	description text not null,
	createdAt timestamp default current_timestamp,
    updatedAt timestamp default current_timestamp,
    deletedAt timestamp default current_timestamp
	
);

CREATE TABLE "questions"
(
	id bigserial not null primary key,
	question varchar(255) not null,
	createdAt timestamp default current_timestamp,
    updatedAt timestamp default current_timestamp,
    deletedAt timestamp default current_timestamp,
    categoryId bigserial,
    CONSTRAINT fk_categoryId FOREIGN KEY (categoryId) REFERENCES "categories" (id)
	
);

CREATE TABLE "options"
(
	id bigserial not null primary key,
	option_text text not null,
	isCorrect text not null,
	createdAt timestamp default current_timestamp,
    updatedAt timestamp default current_timestamp,
    deletedAt timestamp default current_timestamp,
    questionId bigint not null,
    CONSTRAINT fk_questionId FOREIGN KEY (questionId) REFERENCES "questions" (id)
	
);

