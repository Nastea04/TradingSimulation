CREATE DATABASE trading;
USE trading;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    balance DOUBLE NOT NULL
);


CREATE TABLE cryptos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(255)
);


CREATE TABLE holdings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    crypto_symbol VARCHAR(20) NOT NULL,
    quantity DOUBLE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (crypto_symbol) REFERENCES cryptos(symbol) ON DELETE CASCADE
);


CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    crypto_symbol VARCHAR(20) NOT NULL,
    quantity DOUBLE NOT NULL,
    price DOUBLE NOT NULL,
    type ENUM("buy", "sell") NOT NULL,
    time_purchase DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (crypto_symbol) REFERENCES cryptos(symbol) ON DELETE CASCADE
);

