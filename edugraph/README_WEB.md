# EduGraph — Web Dashboard

A browser-based dashboard added on top of the original EduGraph Java project.
Your original CLI (`run_cli.sh` / `main.Main`) is untouched — this adds a second
way to run the project: a small built-in Java HTTP server (`api.ApiServer`,
uses only the JDK's `com.sun.net.httpserver`, no extra dependencies) that
exposes every module as a JSON API, plus a single-page dashboard
(`web/index.html`) that talks to it.

## Run it

```bash
./run_web.sh          # compiles everything, starts server on port 8080
./run_web.sh 9090      # or pick a different port
```

Then open **http://localhost:8080** in your browser.

(`./run_cli.sh` still works exactly as before if you want the original
terminal menu.)

## What's in the dashboard

- **Data** — load the CSVs from `data/`, browse the course catalogue and
  student roster.
- **M1 · BST / AVL** — search `BST_CourseIndex` by course ID, search
  `AVL_StudentDB` by roll number, view in-order traversals.
- **M2 · Segment / Fenwick** — range sum / min / max / average queries over
  student GPA scores.
- **M3 · Graph Traversal** — view the prerequisite graph, run BFS (shortest
  hop path) and DFS (with cycle detection).
- **M4 · Dijkstra / Topo** — weighted shortest learning path by difficulty,
  and a valid course completion order (Kahn's algorithm).
- **M5 · Sorting** — MergeSort by GPA, HeapSort top-K students, QuickSort by
  course popularity, RadixSort on roll numbers.
- **M6 · Knapsack / LIS** — 0/1 and fractional knapsack course selection
  under a credit limit; longest-increasing-subsequence progress analysis for
  a chosen student.
- **M7 · Scheduler** — greedy activity selection, either from
  `data/sessions.csv` or sessions you add directly in the UI.

## How it's wired together

- `src/api/ApiServer.java` — starts an `HttpServer`, holds the in-memory
  `List<Course>` / `List<Student>` (same model classes as the CLI), and maps
  ~20 routes (e.g. `/api/bfs`, `/api/dijkstra`, `/api/knapsack`) to your
  existing algorithm classes in `trees/`, `graphs/`, `sorting/`, `greedy/`.
  It also serves the static files in `web/`.
- `src/api/Json.java` — a tiny hand-rolled JSON reader/writer (no external
  libraries needed, so the project still builds with nothing but the JDK).
- `web/index.html` — vanilla HTML/CSS/JS single page; each tab corresponds to
  one module and calls the matching `/api/...` endpoint with `fetch`.

## Notes

- All data is in-memory only — restarting the server clears it; reload from
  CSV with the "Load default CSVs" button.
- Endpoints generally mirror the CLI's menu options 1:1, so behavior (sort
  order, graph weights, knapsack value formula, etc.) matches the original
  exactly.
