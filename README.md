# Crypto Wallet API

The **Crypto Wallet** project is an API designed to manage and interact with cryptocurrency wallets. It allows users to create wallets, view wallet details, track performance, and maintain wallet history. The API is built with **Spring Boot** and uses **Spring Data JPA** for database operations. It also integrates with external cryptocurrency price APIs for real-time data updates.

## Features

- Create a new wallet.
- Get details of a specific wallet.
- Delete a wallet.
- Update wallet data asynchronously.
- Track wallet performance (e.g., balance, value, etc.).
- View wallet history.
- Interactive API documentation with **Swagger UI**.

## Technologies

- **Spring Boot** 3.x
- **Spring Data JPA** with **H2** in-memory database
- **Springdoc OpenAPI** for API documentation (Swagger UI)
- **Lombok** for boilerplate code reduction
- **JUnit 5** for unit testing
- **Mockito** for mocking dependencies in tests

## Requirements

- Java 17 or higher
- Maven for build management

## Installation

### Clone the Repository

First, clone the repository to your local machine:

```bash
git clone https://github.com/juniorug/crypto-wallet.git
cd crypto-wallet

```
## Usage
```bash
POST /wallets
{
    "name": "Wallet 1",
    "assets": [
        {"symbol": "BTC", "quantity": 1.5, "price": 37870.5058},
        {"symbol": "ETH", "quantity": 10, "price": 2004.9774},
        {"symbol": "USDT", "quantity": 5, "price": 1.0012},
        {"symbol": "SOL", "quantity": 7, "price": 62.5135},
        {"symbol": "BNB", "quantity": 22, "price": 238.8092}
    ]
}
```

## Access OpenAPI docs:
http://localhost:8080/swagger-ui/index.html

http://localhost:8080/api-docs
