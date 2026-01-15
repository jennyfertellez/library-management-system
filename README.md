# Library Management System
A personal library tracking system to manage books and reading progress, 
built to solve the real problem of not knowing which books I already own when browsing bookstores.

## 📖Table of Contents
- [Motivation](#motivation)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Challenges & Solutions](#challenges--solutions)
- [Future Enhancements](#future-enhancements)

## 🎯 Motivation

As a collector of hobbies, my two favorite hobbies is reading and buying books. I know, I know, they are technically 
the same hobby, but I sometimes buy books for the purpose of them looking pretty. The more and more I collected books 
and the more I read them, I began looking for an app that help me track both of my hobbies in one. I tried a variety of 
book apps, but every app felt like it was missing something.

**The Solution:** A simple, fast REST API that lets me:

- Track all books I own with their reading status
- Search my library by title, author, or ISBN 
- Organize books into custom shelves
- Auto-populate book details using ISBN lookup
- Integration with Google Books API to quickly add books
- Track my reading progress and notes

This project represents both a practical solution to a real problem and an opportunity to demonstrate solid backend 
engineering principles.

## ✨ Features

**Core Functionality**

- ✅ **CRUD Operations** - Create, read, update, and delete books
- ✅ **Reading Status Tracking** - Mark books as: To Read, Currently Reading, Finished, or DNF
- ✅ **Advanced Search** - Search across title, author, and description
- ✅ **ISBN Lookup** - Auto-populate book details from Google Books API
- ✅ **Duplicate Detection** - Prevent adding the same book twice
- ✅ **Custom Shelves** - Organize books into personalized collections

**Technical Features**

- ✅ **RESTful API Design** - Clean, intuitive endpoints
- ✅ **Input Validation** - Comprehensive request validation
- ✅ **Error Handling** - Meaningful error messages with proper HTTP status codes
- ✅ **API Documentation** - Interactive Swagger UI
- ✅ **Database Indexing** - Optimized queries for ISBN and title searches
- ✅ **Comprehensive Testing** - Unit and integration tests

## 🛠 Tech Stack:
**Backend Framework**

- **Java 17** - Modern LTS version with latest features
- **Spring Boot 3.2.1** - Industry-standard framework for production applications
- **Spring Data JPA** - Simplifies database operations with repository pattern
- **Hibernate** - Robust ORM for PostgreSQL

**Database**

- **PostgreSQL 15** - Production-grade relational database
- **Docker** - Containerized database for consistent development environment

**Libraries & Tools**

- **Lombok** - Reduces boilerplate code
- **Bean Validation** - Declarative input validation
- **SpringDoc OpenAPI** - Generates interactive API documentation
- **JUnit 5 & Mockito** - Testing framework
- **RestTemplate** - HTTP client for Google Books API integration

**Build Tools**

- **Maven** - Dependency management and build automation

## 🏗 Architecture

| Layered Architecture  |                                             |
|-----------------------|---------------------------------------------|
| Controller Layer      | ← REST endpoints, request/response handling |
| Service Layer         | ← Business logic, validation, orchestration |
| Repository Layer      | ← Data access, JPA queries                  |
| Database (PostgreSql) | ← Data persistence                          |


**Design Patterns Used**

- **Repository Pattern** - Abstracts data access logic
- **DTO Pattern** - Separates API contracts from domain models
- **Service Layer Pattern** - Encapsulates business logic
- **Dependency Injection** - Loose coupling via Spring IoC container

**Package Structure**   

com.yourname.library/   
├── controller/       # REST API endpoints  
├── service/          # Business logic  
├── repository/       # Data access     
├── model/            # JPA entities    
├── dto/              # Data transfer objects   
├── exception/        # Custom exceptions   
└── config/           # Configuration classes   

## 🚀 Getting Started
**Prerequisites**
- Java 17 or higher
- Maven 3.6+
- Docker Desktop
- Git

**Installation**
1. Clone the repository
```
git clone https://github.com/yourusername/library-management-system.git

cd library-management-system
```

2. Start PostgreSQL using Docker
```
docker-compose up -d
```
3. Build the project

`mvn clean install`

4. Run the application

`mvn spring-boot:run`

5. Access the API
- API Base URL: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## 📚 API Documentation
### Book Endpoints

**Create A Book**
```
POST /api/books
Content-Type: application/json

{
  "title": "1984",
  "author": "George Orwell",
  "isbn": "9780451524935",
  "description": "Dystopian novel",
  "status": "TO_READ"
}
```
**Get All Books**
```
GET /api/books
```
**Get Book by ID**
```
GET /api/books/{id}
```
**Update Book**
```
PUT /api/books/{id}
Content-Type: application/json

{
  "status": "CURRENTLY_READING"
}
```
**Delete Book**
```
DELETE /api/books/{id}
```
**Search Books**
```
GET /api/books/search?term=hobbit
```
**Lookup Book by ISBN (Google Books API)**
```
POST /api/books/isbn/{isbn}
```

### Shelf Endpoints

**Create A Shelf**
```
POST /api/shelves
Content-Type: application/json

{
  "name": "Sci-Fi Favorites",
  "description": "My favorite science fiction books"
}
```

**Add Book to Shelf**
```
POST /api/shelves/{shelfId}/books/{bookId}
```

Full API documentation available at `/swagger-ui.html` when running

## Database Schema
**Books Table**
```sql
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    isbn VARCHAR(13) UNIQUE,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    description VARCHAR(2000),
    published_date VARCHAR(50),
    page_count INTEGER,
    thumbnail_url VARCHAR(500),
    status VARCHAR(50) NOT NULL,
    finished_date DATE,
    rating INTEGER,
    notes VARCHAR(2000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX idx_isbn ON books(isbn);
CREATE INDEX idx_title ON books(title);
```

**Shelves Table (Many-to-Many)**
```sql
CREATE TABLE shelves (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE book_shelf (
    book_id BIGINT REFERENCES books(id),
    shelf_id BIGINT REFERENCES shelves(id),
    PRIMARY KEY (book_id, shelf_id)
);
```

**Entity Relationships**
- Book ↔ Shelf: Many-to-Many (a book can be in multiple shelves, a shelf contains multiple books)

## 💡 Challenges & Solutions:
TBA

## 🧪 Testing
**Test Coverage**

- **Unit Tests:** Service layer business logic (Mockito for mocking)
- **Integration Tests:** Repository layer with actual database (@DataJpaTest)
- **Controller Tests:** API endpoints using MockMvc (@WebMvcTest)

**Running Test**
```
# Run all tests
mvn test

# Run with coverage report
mvn test jacoco:report
```

## 🔮 Future Enhancements
To be added:
- **User Authentication** - Spring Security with JWT tokens
- **Reading Goals** - Track books read per year, pages per day
- **Book Recommendations** - ML-based suggestions from reading history
- **Export/Import** - CSV/Excel export for book lists
- **Mobile App** - React Native frontend
- **Barcode Scanner** - Scan ISBN via camera
- **Social Features** - Share reading lists with friends
- **Advanced Analytics** - Reading patterns, favorite genres, statistics dashboard

👤 **Author**
Jennifer Tellez Vera

- **GitHub**: @jennyfertellez
- **LinkedIn**: [Jennifer Tellez Vera](https://www.linkedin.com/in/jennifer-tellez-vera/)