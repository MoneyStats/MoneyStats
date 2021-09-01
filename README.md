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

<code>create database moneystats;</code> <br>
<code>use moneystats; </code> <br>
<code>insert into users(first_name, last_name, date_of_birth, email, username, password, role) values </code> <br>
<code>("Admin", "User", "NaN", "admin@email.com", "admin", "admin", "ADMIN"); </code> <br>
<code>select * from users; </code> <br>
<code>delete from users; </code> <br>
<br>
<code>insert into categories(name) values </code><br>
<code>("Contanti"),  </code><br>
<code>("Carte di credito"), </code><br>
<code>("Carte di debito"), </code><br>
<code>("Mutui"), </code><br>
<code>("Conto Corrente"), </code><br>
<code>("Risparmi"), </code><br>
<code>("Cupon"), </code><br>
<code>("Investimenti"), </code><br>
<code>("Altro"); </code><br>
<code>select * from categories; </code><br>
<br>
<code>insert into wallets(name, category_id, user_id) values </code><br>
<code>("Ledger", 5, 2), </code><br>
<code>("Coinbase", 5, 2), </code><br>
<code>("Binance", 5, 2), </code><br>
<code>("Contanti", 1, 2), </code><br>
<code>("Salvadanaio", 3, 2), </code><br>
<code>("Revolut", 2, 2), </code><br>
<code>("BNL", 2, 2), </code><br>
<code>("Amazon", 4, 2); </code><br>
<code>select * from wallets; </code><br>
<br>
<code>insert into statements(date, value, user_id, wallet_id) values </code><br>
<code>("2021-06-09", 100, 2, 1), </code><br>
<code>("2021-06-09", 200, 2, 2), </code><br>
<code>("2021-06-09", 300, 2, 3), </code><br>
<code>("2021-06-09", 400, 2, 4), </code><br>
<code>("2021-06-09", 500, 2, 5), </code><br>
<code>("2021-06-09", 600, 2, 6), </code><br>
<code>("2021-06-09", 700, 2, 7), </code><br>
<code>("2021-06-09", 800, 2, 8); </code><br>
<code>select * from statements; </code>
