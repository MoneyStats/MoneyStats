# MoneyStats

This project is developed with a different logic and implementations than MoneyStats [OLD]. <br>
The idea is to develop a web applications that can allow the user to be able to keep track of his assets. <br>
Once registered and logged in, the user will have at his disposal a series of default categories (cash, credit cards, investments, etc.), before proceeding to add the statement, he must proceed with the creation of the respective wallet, associating them according to the categories. <br>
Subsequently, an ADMIN management screen will be implemented. <br>

First of all, use MySQL and create the database, then start the project, it will create the tables automatically. <br>
Go back on MySQL and insert the user_admin, then you are ready to start using MoneyStats, just go on <strong>http://localhost:8080/loginPage.html</strong> and create an new user. <br>
<hr>
<strong>MySQL Database MoneyStats script</strong>
<hr>

<pre>
create database moneystats;
use moneystats;

insert into users(first_name, last_name, date_of_birth, email, username, password, role) values
("Admin", "User", "NaN", "admin@email.com", "admin", "admin", "ADMIN");
select * from users;

insert into categories(name) values
("Contanti"),
("Carte di credito"),
("Carte di debito"),
("Mutui"),
("Conto Corrente"),
("Risparmi"),
("Cupon"),
("Investimenti"),
("Altro");
select * from categories;

insert into wallets(name, category_id, user_id) values
("Ledger", 5, 2),
("Coinbase", 5, 2),
("Binance", 5, 2),
("Contanti", 1, 2),
("Salvadanaio", 3, 2),
("Revolut", 2, 2),
("BNL", 2, 2),
("Amazon", 4, 2);
select * from wallets;

insert into statements(date, value, user_id, wallet_id) values
("2021-06-09", 100, 2, 1),
("2021-06-09", 200, 2, 2),
("2021-06-09", 300, 2, 3),
("2021-06-09", 400, 2, 4),
("2021-06-09", 500, 2, 5),
("2021-06-09", 600, 2, 6),
("2021-06-09", 700, 2, 7),
("2021-06-09", 800, 2, 8);
select * from statements; </pre>
