/*CREATE TABLE IF NOT EXISTS users
(
    id bigint primary key auto_increment,
    first_name varchar(50),
    last_name varchar(50),
    date_of_birth varchar(50),
    email varchar(50),
    username varchar(50),
    password varchar(200),
    role varchar(50)
);*/

CREATE TABLE IF NOT EXISTS categories
(
    id int primary key auto_increment,
    name varchar(50)
);
