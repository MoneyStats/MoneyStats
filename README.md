# MoneyStats

![Cattura](https://user-images.githubusercontent.com/83874719/133930684-9b495725-5939-4f41-b96f-41aa1ebaf474.PNG)
This project is developed with a different logic and implementations than MoneyStats [OLD]. <br>
The idea is to develop a web applications that can allow the user to be able to keep track of his assets. <br>
Once registered and logged in, the user will have at his disposal a series of default categories (cash, credit cards, investments, etc.), before proceeding to add the statement, he must proceed with the creation of the respective wallet, associating them according to the categories. <br>
Subsequently, an ADMIN management screen will be implemented. <br>

First of all, use MySQL and create the database, then start the project, it will create the tables automatically. <br>
Go back on MySQL and insert the user_admin, then you are ready to start using MoneyStats, just go on <strong>http://localhost:8080/loginPage.html</strong> and create an new user. <br>
<hr>
<strong>MySQL Database MoneyStats script for testing</strong>
<hr>

<pre>
create database moneystats;
use moneystats;

[Admin not working]
insert into users(first_name, last_name, date_of_birth, email, username, password, role) values
("Admin", "User", "NaN", "admin@email.com", "admin", "admin", "ADMIN");
select * from users;

insert into categories(name) values
('Cash'),
('Credit Card'),
('Debit Card'),
('Recurrence'),
('Bank Account'),
('Save'),
('Coupon'),
('Check'),
('Investments'),
('Other');
select * from categories;

insert into wallets(name, category_id, user_id) values
("Ledger", 5, 2),
("Coinbase", 5, 2),
("Binance", 5, 2),
("Cash", 1, 2),
("Safer", 3, 2),
("Revolut", 2, 2),
("BNL", 2, 2),
("Amazon", 4, 2);
select * from wallets;

insert into statements(date, value, user_id, wallet_id) values
("01-06-2021", 100, 2, 1),
("01-06-2021", 200, 2, 2),
("01-06-2021", 300, 2, 3),
("01-06-2021", 400, 2, 4),
("01-06-2021", 500, 2, 5),
("01-06-2021", 600, 2, 6),
("01-06-2021", 700, 2, 7),
("01-06-2021", 800, 2, 8),
("02-06-2021", 100, 2, 1),
("02-06-2021", 200, 2, 2),
("02-06-2021", 300, 2, 3),
("02-06-2021", 400, 2, 4),
("02-06-2021", 500, 2, 5),
("02-06-2021", 600, 2, 6),
("02-06-2021", 700, 2, 7),
("02-06-2021", 800, 2, 8),
("03-06-2021", 100, 2, 1),
("03-06-2021", 200, 2, 2),
("03-06-2021", 300, 2, 3),
("03-06-2021", 400, 2, 4),
("03-06-2021", 500, 2, 5),
("03-06-2021", 600, 2, 6),
("03-06-2021", 700, 2, 7),
("03-06-2021", 800, 2, 8);
select * from statements; </pre>

![Cattura1](https://user-images.githubusercontent.com/83874719/133930703-111bf64b-3a59-4d83-8556-64e22a92e04a.PNG)
