# Library Management System
A personal library tracking system to manage books and reading progress, 
built to solve the real problem of not knowing which books I already own when browsing bookstores.

## Live Demo
- API Documentation: (http://localhost:8080/swagger-ui.html) (when running locally)

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

**Book Management**

- ✅ **CRUD Operations** - Create, read, update, and delete books
- ✅ **Reading Status Tracking** - Mark books as: To Read, Currently Reading, Finished, or DNF
- ✅ **Advanced Search** - Search across title, author, and description
- ✅ **Multi-Source Book Lookup** - Auto-populate book detials from OpenLibrary, Google Books, and Jikan (manga)
- ✅ **Manga Support** - Dedicated manga search via Jikan API with auto-detection
- ✅ **Smart Search** - Search by ISBN or title with results from multiple sources
- ✅ **Duplicate Detection** - Prevent adding the same book twice
- ✅ **Custom Shelves** - Organize books into personalized collections

**Reading Analytics**
- ✅ **Statistics Dashboard** - View reading progress and insights
- ✅ **Top Authors** - See your most-read authors
- ✅ **Reading Pace** - Books per month and yearly trends
- ✅ **Rating Distribution** - Visualize your book ratings

**Organization** 
- ✅ **Custom Shelves** - Create personalized book collections
- ✅ **Shelf Management** - Organized books into multiple shelves
- ✅ **Many to Many** - Books can belong to multiple shelves

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
- **RestTemplate** - HTTP client for external API integration

**External APIs**
- **OpenLibrary API** - Primary book data source (no rate limits)
- **Google Books API** - Secondary book data source with ISBN lookup
- **Jikan API** - Manga database (MyAnimeList integration)

**Frontend Framework**
- **React** - UI Library
- **Typescript** - Type-safe JavaScript
- **Vite** - Fast build tool and dev server
- **Tailwind CSS** - Utility-first styling
- **Axios** - HTTP client for API calls
- **Lucide React** - Icon library

**Infrastructure**

- **Vercel** - PostgreSQL containerization
- **Docker** - Containerized database for consistent development environment

## 🏗 Architecture

| Layered Architecture  |                                             |
|-----------------------|---------------------------------------------|
| Controller Layer      | ← REST endpoints, request/response handling |
| Service Layer         | ← Business logic, validation, orchestration |
| Repository Layer      | ← Data access, JPA queries                  |
| Database (PostgreSql) | ← Data persistence                          |
 

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
**Backend Setup**
2. Start PostgreSQL using Docker
```
docker-compose up -d
```
3. Build the project

`mvn clean install`

4. Run the application

`./mvnw spring-boot:run`

5. Backend will be available
- API Base URL: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

**Frontend Setup**
6. Navigate to frontend directory
```
cd library-frontend
```
7. Install dependencies
`npm install`
8. Start development server
`npm run dev`

9. Frontend will be available: `http://localhost:5173`

## 📚 API Documentation
### Books

| Method     | Endpoint                     | Description                                     |
|------------|------------------------------|-------------------------------------------------|
| `GET`      | `/api/books`                 | Get all books (paginated)                       |
| `GET`      | `/api/books/{id}`            | Get books by ID                                 |
| `POST`     | `/api/books`                 | Create a new book                               |
| `PUT`      | `/api/books/{id}`            | Uodate a book                                   |
| `DELETE`   | `/api/books/{id}`            | Delete a book                                   |
| `GET`      | `/api/books/search`          | Search books by term                            |
| `POST`     | `/api/books/isbn/{isbn}`     | Create book from ISBN (multi-source)            |
| `GET`      | `/api/books/search/title`    | Search by title (supports manga)                | 
| `GET`      | `/api/books/search/all`      | Search all sources (OpenLibrary, Google, Jikan) |
| `GET`      | `/api/books/stats`           | Get reading statistics                          |
| `GET`      | `/api/books/advanced-search` | Advanced search with filters                    |


### Shelves
| Method   | Endpoint                                 | Description            |
|----------|------------------------------------------|------------------------|
| `GET`    | `/api/shelves`                           | Get all shelves        |
| `GET`    | `/api/shelves/{id}`                      | Get shelf by ID        |
| `POST`   | `/api/shelves/`                          | Create a new shelf     |
| `PUT`    | `/api/shelves/{id}`                      | Update a shelf         |
| `DELETE` | `/api/shelves/{id}`                      | Delete a shelf         |
| `POST`   | `/api/shelves/{shelfId}/books/{bookId}`  | Add to shelf           |
| `DELETE` | `/api/shelves/{shelfId}/books/{bookId}`  | Remove book from shelf |


### Example Requests
**Create A Book**
```
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Hobbit",
    "author": "J.R.R. Tolkien",
    "isbn": "9780547928227",
    "status": "TO_READ"
  }'
```
**Lookup Book by ISBN:**
```
curl -X POST http://localhost:8080/api/books/isbn/9780547928227
```
**Search All Sources (Multi-Source):**
```bash
# Search by ISBN across all sources 
curl "http://localhost:8080/api/books/search/all?query=9780545010221"

# Search by title (especially for manga)
curl "http://localhost:8080/api/books/search/title?title=One Piece"
```

**Get Reading Stats**
```
curl http://localhost:8080/api/books/stats
```
**Advance Search**
```
curl "http://localhost:8080/api/books/advanced-search?status=FINISHED&minRating=4&minYear=2020"
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
                       id                BIGSERIAL PRIMARY KEY,
                       isbn              VARCHAR(13) UNIQUE,
                       title             VARCHAR(255) NOT NULL,
                       author            VARCHAR(255),
                       description       TEXT,
                       published_date    VARCHAR(50),
                       page_count        INTEGER,
                       thumbnail_url     VARCHAR(500),
                       status            VARCHAR(50) NOT NULL,
                       finished_date     DATE,
                       rating            INTEGER,
                       notes             TEXT,
                       created_at        TIMESTAMP NOT NULL,
                       updated_at        TIMESTAMP
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
## 💡 Challenges & Solutions

### 1. **Google Books API Rate Limiting**
**Challenge:** Google Books API has a daily quota limit (free tier: 1,000 requests/day), which was easily exceeded during development and testing.

**Solution:** Implemented a multi-source fallback system:
- **Primary:** OpenLibrary API (no rate limits)
- **Secondary:** Google Books API (when OpenLibrary fails)
- **Tertiary:** Jikan API for manga

This ensures book lookups always succeed, even when one API is unavailable.

### 2. **Manga Coverage Gap**
**Challenge:** Traditional book APIs (Google Books, OpenLibrary) have limited manga coverage and often miss volumes or series.

**Solution:** Integrated Jikan API (MyAnimeList database) specifically for manga:
- Auto-detects manga based on title patterns
- Falls back to Jikan when manga is detected
- Provides comprehensive manga metadata (volumes, chapters, Japanese titles)

### 3. **Ambiguous Search Results**
**Challenge:** Different APIs return different results for the same query. Users couldn't confirm they were adding the correct book.

**Solution:** Built a multi-source search preview:
- Shows results from all APIs side-by-side
- Users can compare and select the most accurate result
- Displays source badges (OpenLibrary, Google, Jikan) for transparency

## 🧪 Testing
**Test Coverage**

- **Unit Tests:** Service layer business logic (Mockito for mocking)
- **Integration Tests:** Repository layer with actual database (@DataJpaTest)
- **Controller Tests:** API endpoints using MockMvc (@WebMvcTest)

**Running Test**
```
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```
**Test Coverage: 70%+**

## Project Structure
```
library-management-system/
├── src/main/java/com/jennifertellez/library/
│   ├── controller/           # REST API endpoints
│   ├── service/              # Business logic
│   ├── repository/           # Data access layer
│   ├── model/                # JPA entities
│   ├── dto/                  # Data transfer objects
│   ├── exception/            # Custom exceptions
│   └── config/               # Configuration classes
├── src/main/resources/
│   └── application.properties
├── src/test/                 # Unit and integration tests
├── library-frontend/         # React frontend
│   ├── src/
│   │   ├── components/       # Reusable UI components
│   │   ├── pages/            # Page components
│   │   ├── services/         # API service functions
│   │   ├── types/            # TypeScript interfaces
│   │   └── utils/            # Helper functions
│   └── public/
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Key Design Decisions
### Backend Architecture

### Frontend Architecture
- **Layered Architecture** - Clear separation: Controller → Service → Repository
- **DTO Pattern** - API layer separated from domain models for flexibility and security
- **Custom Exceptions** - Domain-specific errors with meaningful messages
- **Pagination** - Scalable handling of large datasets (O(1) retrieval with indexed queries)
- **Database Indexing** - Optimized search performance on frequently queried fields

### Frontend Architecture
- **Component-Based** - Reusable, maintainable React components
- **TypeScript** - Type safety prevents runtime errors, similar to Java's compile-time checking
- **State Management** - React hooks for local state, no global state library needed
- **Responsive Design** - Mobile-first approach with Tailwind utilities

### API Integration
- **Google Books API** - Automatic book data population via ISBN
- **Error Handling** - Graceful degradation when external API fails
- **RestTemplate** - Spring's HTTP client for third-party integration

## 🔮 Future Enhancements

**In Progress (Week 1-2):**
- ✅ Multi-source book search (OpenLibrary, Google Books, Jikan)
- ✅ Manga support with Jikan API integration
- 🚧 Reading Goals - Track books read per year, pages per day
- 🚧 Barcode Scanner - Manual ISBN entry (camera scan coming next)

**Planned (Week 3-4):**
- **Camera Barcode Scanner** - Scan ISBN via device camera
- **Enhanced Book Details** - More metadata and book insights
- **Import/Export** - CSV/Excel export for book lists

**Future (Post-March 2026):**
- **User Authentication** - Spring Security with JWT tokens (foundation complete)
- **Book Recommendations** - ML-based suggestions from reading history
- **Mobile App** - React Native frontend
- **Social Features** - Share reading lists with friends
- **Advanced Analytics** - Reading patterns, favorite genres, statistics dashboard

👤 **Author**
Jennifer Tellez Vera

- **GitHub**: @jennyfertellez
- **LinkedIn**: [Jennifer Tellez Vera](https://www.linkedin.com/in/jennifer-tellez-vera/)