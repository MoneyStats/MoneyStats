CREATE TABLE categories
(
    id   int primary key auto_increment,
    name varchar(50)
);
INSERT INTO categories(name)
VALUES ('Cash'),
       ('Credit Card'),
       ('Debit Card'),
       ('Recurrence'),
       ('Bank Account'),
       ('Safe'),
       ('Coupon'),
       ('Investments'),
       ('Other');

