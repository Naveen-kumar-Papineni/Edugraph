<p align="center">
  <img src="Screenshot%202026-06-25%20162030.png" alt="EduGraph WebUI" width="700"/>
</p>

# EduGraph – Adaptive Learning Dashboard

A full-stack Java web application that demonstrates data structures, algorithms, and database integration through an interactive dashboard for managing courses, students, and academic performance data.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java, Spring Boot 3.2.0, Spring Data JPA |
| Database | PostgreSQL |
| Build | Maven |
| Frontend | Vanilla HTML + CSS + JavaScript |
| Algorithms | BST, AVL, Segment Tree, Fenwick Tree, BFS, DFS, Dijkstra, Topological Sort, Merge Sort, Quick Sort, Heap Sort, Radix Sort, 0/1 Knapsack, LIS, Activity Selection |

## Features

- **Data Management** – CRUD operations for courses, students, and results via REST API with PostgreSQL persistence
- **Tree Structures** – BST keyed on course ID and self-balancing AVL tree keyed on student roll number
- **Range Queries** – Segment Tree and Fenwick Tree for GPA score analytics
- **Graph Algorithms** – BFS shortest path, DFS cycle detection, Dijkstra's weighted shortest path, Topological sort on prerequisite graph
- **Sorting** – Merge Sort, Quick Sort, Heap Sort, and Radix Sort over live data
- **Optimization** – 0/1 Knapsack and Fractional Knapsack for course selection, LIS for student progress tracking
- **Scheduling** – Greedy activity selection for study session planning

## Quick Start

### Prerequisites
- Java 21+
- PostgreSQL running on `localhost:5432`
- Database `edugraph` with user `postgres`

### Run
```bash
# Update password in application.properties if needed
.\mvnw.cmd spring-boot:run
```

Open **http://localhost:8081** and click *Load default CSVs* to seed sample data.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/courses` | List all courses |
| POST | `/api/courses` | Add a course |
| GET | `/api/students` | List all students |
| POST | `/api/students` | Add a student |
| POST | `/api/students/{roll}/results` | Add a result |
| POST | `/api/students/{roll}/enrol` | Enrol student in course |
| GET | `/api/bst/search` | BST search by course ID |
| GET | `/api/avl/search` | AVL search by roll number |
| GET | `/api/segment` | Segment tree range query |
| GET | `/api/fenwick` | Fenwick tree range sum |
| GET | `/api/bfs` | BFS traversal / shortest path |
| GET | `/api/dfs` | DFS with cycle detection |
| GET | `/api/dijkstra` | Dijkstra's shortest path |
| GET | `/api/toposort` | Topological course order |
| GET | `/api/sort/students` | Merge sort by GPA |
| GET | `/api/sort/topstudents` | Heap sort top-K |
| GET | `/api/sort/courses` | Quick sort by popularity |
| GET | `/api/knapsack` | 0/1 and fractional knapsack |
| GET | `/api/lis` | Longest increasing subsequence |
| GET | `/api/schedule` | Activity selection scheduler |

## Project Structure

```
EduGraph_WebUI/
├── pom.xml
├── mvnw.cmd
├── run_web.bat
├── README.md
├── edugraph/
│   ├── data/                  # CSV data files
│   ├── web/
│   │   └── index.html         # Frontend UI
│   └── src/
│       ├── app/               # Spring Boot application
│       │   ├── EduGraphApplication.java
│       │   ├── controller/    # REST endpoints
│       │   ├── service/       # Business logic
│       │   ├── model/         # JPA entities
│       │   ├── repository/    # Spring Data repos
│       │   └── config/        # App config & initializer
│       ├── core/              # Domain models
│       ├── trees/             # BST & AVL implementations
│       ├── graphs/            # Graph algorithms
│       ├── sorting/           # Sorting algorithms
│       ├── greedy/            # Greedy & DP algorithms
│       ├── main/              # CSV loader utilities
│       └── api/               # Legacy HTTP server (archived)
```

Icons and diagrams created with [Excalidraw](https://excalidraw.com).
