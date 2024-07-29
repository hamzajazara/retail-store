# Project Name

## Introduction

Retail Store Discounts calculation system for a retail store. It applies specific discounts based on user type and purchased items.

### Discount Rules
- Employee Discount: If the user is an employee of the store, then will receive a 30% discount.
- Affiliate Discount: If the user is an affiliate of the store, then will receive a 10% discount.
- Loyal Customer Discount: If the user has been a customer for over 2 years, then will receive a 5% discount.
- Bulk Purchase Discount: For every $100 on the bill, a discount will be a $5.

## Prerequisites

Before you begin, ensure you have the following software installed on your computer:
- Java 21
- Docker and Docker Compose

## Setting Up

### 1. Download the Project

First, download the project files to your computer.

### 2. Run Docker and Docker Compose

To start the Mongo server run the following docker-compose command:

`docker-compose up`

### 3. Start the Retail Store

To start and test the apis run the following command:
`mvn clean install`

then,
`mvn spring-boot:run`

## Api Calls

To check and call the api with an examples:

### Employee Discount Example
`curl -u user:password -X GET "http://localhost:8080/api/bill/amount?billId=1&userId=1" -H "accept: application/json"`

### Affiliate Discount Example
`curl -u user:password -X GET "http://localhost:8080/api/bill/amount?billId=2&userId=2" -H "accept: application/json"`

### Customer Discount Example
`curl -u user:password -X GET "http://localhost:8080/api/bill/amount?billId=3&userId=3" -H "accept: application/json"`