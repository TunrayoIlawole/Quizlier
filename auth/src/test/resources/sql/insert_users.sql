INSERT INTO auth_service.user (
    firstName, lastName, userName, email, userRole, password
)
VALUES
('John', 'Doe', 'johnDoe', 'johndoe@example.com', 'ADMIN'::user_role, 'password');