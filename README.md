
# Shop Application (Multi-Module Project)

![status](https://img.shields.io/badge/status-in%20progress-yellow)

> 🚧 This project is still under development. Features may change frequently.

## Overview

This project is a **multi-module backend application** built with **Java** and **Spring Boot**.
It simulates the backend of an e-commerce platform with modular separation of concerns.
Each module handles a specific domain such as authentication, product management, ordering, payment, and inventory.

The goal is to practice **modular monolith architecture**, **secure authentication/authorization**, and **event-driven communication** within a single project.

---

## Features

* **Authentication & Authorization**

    * JWT-based authentication with Spring Security
    * Refresh token caching using Redis
    * HttpOnly cookies for refresh token storage
    * Role-based authorization for modules

* **CRUD Operations**

    * Hard-delete and soft-delete implemented across entities
    * Modular separation for product, order, payment, and inventory management

* **Product Module**

    * Manage products and product variants
    * Support for placing product orders

* **Order Module**

    * Order lifecycle management with status flow:
      `pending_verification → confirmed → paid`
    * Validation of product details before confirming orders

* **Payment Module**

    * Cash on Delivery (COD) payment support
    * Updates order status after successful payment

* **Event-Driven Workflow (with RabbitMQ)**

    * Order module verifies product details via events with Product module
    * Order module communicates with Payment module to process total price
    * Payment module confirms successful payment back to Order module

---

## Project Structure

```
shop/
├── shop-app/          # Main application entry point
├── shop-auth/         # Authentication & authorization
├── shop-core/         # Shared core utilities, DTOs, and configs
├── shop-inventory/    # Inventory management
├── shop-order/        # Order lifecycle and status management
├── shop-payment/      # Payment handling (COD, status updates)
├── shop-product/      # Product and product variant management
├── shop-user/         # User management
├── uploads/           # Static or uploaded resources
├── pom.xml            # Parent Maven POM
└── README.md          # Project documentation
```

---

## Tech Stack

* Java 17+
* Spring Boot
* Spring Security + JWT
* Redis
* RabbitMQ
* Maven (multi-module)
* H2 DB

---

## Getting Started

### Prerequisites

* Java 17+
* Maven
* Docker (to run Redis and RabbitMQ)

### Run Dependencies

```bash
# Start Redis and RabbitMQ
docker-compose up -d
```

### Build and Run

```bash
# Clean and install all modules
mvn clean install

# Run the main application
mvn spring-boot:run -pl shop-app
```

---

## Future Improvements

* Add frontend integration (React)
* Extend payment module with online payment gateways (Stripe, VNPAY)
* Add API Gateway and Service Discovery (if migrating to microservices)
* Implement CI/CD pipelines

