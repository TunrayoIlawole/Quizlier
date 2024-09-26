INSERT INTO core_service.category (
    name, description
)
VALUES
('History', 'How good are you in world history?');

INSERT INTO core_service.question (
    question, categoryId
)
VALUES
('Who was the fourth president of the United States?', 1);

INSERT INTO core_service."option" (
    optionText, isCorrect, questionId
)
VALUES
('James Madison', True, 1),
('John Adams', False, 1),
('Abraham Lincoln', False, 1);