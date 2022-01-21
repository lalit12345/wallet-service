# wallet-service
A service to manage Credit/Debit entries for a player

## Tech stack
- Spring Boot
- H2 in-memory db

## Database Setup
- No need of any setup as spring boot has embedded in-memory H2 db. But there is ***data.sql*** file (placed in classpath) used to insert the data in table on server start-up
- H2 condole can be accessed using http://localhost:8080/v1/accounts/h2-console/login.jsp URL. Password is mentioned in ***src/main/resources/application.properties*** which can be changed
- Once connected, please use ***SELECT * FROM ACCOUNT;*** command to see the sample data inserted

## How to run:
1. Run command ***mvn spring-boot:run*** from project folder
2. URL for swagger: http://localhost:8080/v1/accounts/swagger-ui/index.html OR ***API_Schema.json*** (kept in project folder) can be imported into postman for accessing the API's

## API's available
1. Create account
2. Fetch balance
3. Debit transaction
4. Credit transaction
5. View all transactions for a user
