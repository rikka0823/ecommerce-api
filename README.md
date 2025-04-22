# ecommerce-api

`ecommerce-api` 是一個基於 Spring Boot 的後端應用程式，旨在提供**電子商務系統**所需的 API 功能，包括商品管理、訂單處理和會員管理等。

## Project Structure

專案架構（User Story、ERD、System Architecture），請參考 [`電子商務系統`](https://drive.google.com/file/d/1qIYqVUoTaD-in_QOWHMfeVrjp2icjEm7/view?usp=sharing)。

## Key Features

- **商品管理**：提供查詢商品列表（支援分頁、排序、依類別和關鍵字篩選）模糊搜尋，依 ID 查詢商品、新增商品、更新商品和刪除商品等功能。
- **訂單管理**：提供依用戶 ID 創建訂單、查詢用戶訂單列表（支援分頁）、依用戶 ID 和訂單 ID 刪除訂單等功能。
- **會員管理**：提供用戶註冊、登入、依 Email 查詢用戶資訊、依 Email 更新用戶角色、依用戶 ID 刪除帳號等功能。

## Tech Stack

此專案使用以下技術：

- **Java 17**：作為開發語言。
- **Spring Boot 3.4.3**：作為應用核心框架，簡化開發流程並提升可維護性，並搭配 **Maven** 進行構建與依賴管理。
- **Lombok**：透過註解簡化物件開發流程，並以 @Builder 實現建構者模式（Builder Pattern），提升物件建立的可讀性與維護性。
- **Spring JDBC**：採用底層 JDBC 與資料庫連接。實作上，以動態拼接 SQL 語法，動態調整列表查詢方式、處理批量更新（batchUpdate），並結合悲觀鎖（Pessimistic Lock）處理超賣等問題。相較於 ORM 框架（如 JPA），更具彈性。也避免額外的物件映射層，帶來更好的效能。
- **Spring Validation**：以 @Valid、@Validated 處理 Exception，並建立 GlobalExceptionHandler，採用 @ExceptionHandler 客製化錯誤、例外返回 JSON 訊息。
- **Spring Security**：以 git 分支分別實作 Token-Based Authentication（main）、Session-Based Authentication（feature/session-auth），並對 JWT、CORS、CSRF 及資源請求路徑，作相關驗證及權限設定。
- **Keycloak 25.0.6**：採用開源軟體架設 Authorization Server，與 Spring Security 整合，支援 JWT 驗證和授權，實作 OAuth 2.0 授權碼流程（Authorization code flow）。
- **OAuth 2.0**：串接 Google、Facebook 社交登入（Social Login），提供用戶第三方平台註冊與登入。
- **MySQL 8.0.40**：關聯式資料庫管理系統，欄位設計以一對一、一對多、多對多，及採用角色存取控制（RBAC）模型方式，儲存用戶資訊、角色、商品等資訊；並結合 Spring Boot 的 @Transactional，處理 ACID 之交易特性。
- **Redis**：作為非關聯式資料庫，採用 Cache Aside Pattern，儲存熱門查詢資訊，輔助 MySQL，減少資料庫讀取壓力。 
- **H2 Database**：嵌入式資料庫，採 In-Memory 模式，結合單元測試，不持久化資料，建立乾淨測試環境。
- **JUnit 5**：針對重要商業邏輯撰寫單元測試，如用戶註冊，訂單、列表查詢等，確保程式的穩定性與可靠性，程式碼覆蓋率（Code Coverage），約70%。
- **SLF4J、Logback**：透過 SLF4J 撰寫登入行為紀錄，搭配 LoggerFactory 記錄 OAuth 2.0 與 JWT 登入方式，詳細記錄登入帳號、User-Agent 與時間等資訊，有助於使用者行為分析、問題排查與資安稽核。
- **Springdoc OpenAPI**：用於生成 Swagger UI 文件，詳細描述 API 端點。
- **Nginx**：靜態網頁資源的存放伺服器，模擬前端授權碼流程，作為 OAuth 2.0 流程中的重定向頁面（redirect URIs）。
- **Docker**：編寫 Dockerfile 以建立前端跳轉頁面（web）、MySQL 資料庫的容器映像檔（image），並使用 Docker Compose 管理多容器架構，整合 Nginx、MySQL、Redis，建立開發一致環境。

## Running the Project in Docker and OAuth 2.0

- Docker：
  
  在執行以下 Shell 指令前，請先確保電腦安裝及啟動 Docker：
  
  - `docker-compose build --no-cache`：建立 Spring Boot 應用程式和 MySQL 資料庫的 Docker 映像檔（image），並強制不使用 Cache。
  - `docker-compose up -d`：啟動 docker-compose，並在 Docker daemon 背景中運作所有容器。
  - `docker-compose down --volumes`： 停止、移除運作中的容器，並且移除相關的映像檔（image）及本地 Docker Volume 中存放的資料庫資料。
  
  容器順利運作後，請於網址列直接輸入 `localhost`，可看見跳轉頁面，以及 Keycloak 相關設定。

- OAuth 2.0
  
   - Keycloak：
  
     在執行以下 Shell 指令前，本專案架設的是 [`Keycloak 25.0.6`](https://www.keycloak.org/2024/09/keycloak-2506-released) 版本，請先至官網下載，及根據跳轉頁面上所指示內容進行相關設定：

      - `bin\kc.bat start-dev --http-port 5500`：執行後，請於網址列直接輸入 `localhost:5500`，可進入 Keycloak 進行相關設定。
  
    - Google、Facebook：
     
      如果要操作第三方登入功能，請先至兩者官方網站之開發者頁面申請 `client-id`、`client-secret`，並於 Spring Boot 之 `application.properties` 進行設定。

容器、OAuth 2.0 順利設定完成後，API 相關操作，可參考 [`Swagger UI`](http://localhost:8080/swagger-ui/index.html) 內 API 文件操作範例，或將本專案之 `ecommerce-api.postman_collection.json` 匯入 Postman 查看範例，並依需求輸入 Shell 指令停止、移除運作中的容器，停止服務。

## API Endpoints

### 商品管理 API

- `GET /products`
  查詢商品列表，支援以下查詢參數：
    - `category` （可選）: 商品類別。
    - `search` （可選）: 關鍵字搜尋。
    - `orderBy` （可選）: 排序欄位（預設為 `LAST_MODIFIED_DATE`）。
    - `sort` （可選）: 排序方式 （`ASC` 或 `DESC`，預設為 `DESC`）。
    - `limit` （可選）: 每頁筆數（預設為 `5`，最大 `25`）。
    - `offset`（可選）: 跳過筆數（預設為 `0`）。
  
  返回分頁的商品列表。

- `GET /products/{productId}`
  根據商品 ID 查詢商品詳細資訊。

- `POST /products`
  新增商品。請求體為 `ProductDTO`。

- `PUT /products/{productId}`
  更新指定 ID 的商品資訊。請求體為 `ProductDTO`。

- `DELETE /products/{productId}`
  刪除指定 ID 的商品。

### 訂單管理 API

- `POST /users/{userId}/orders`
  為指定用戶 ID 創建訂單。請求體為 `OrdersDTO`。

- `GET /users/{userId}/orders`
  查詢指定用戶 ID 的訂單列表，支援以下查詢參數：
    - `limit`（可選）: 每頁筆數（預設為 `5`，最大 `25`）。
    - `offset`（可選）: 跳過筆數（預設為 `0`）。
  
  返回分頁的訂單列表。

- `DELETE /users/{userId}/orders/{orderId}`
  刪除指定用戶 ID 和訂單 ID 的訂單。

### 會員管理 API

- `POST /users/register`
  註冊新用戶。請求體為 `UserDTO`。

- `POST /users/login`
  用戶登入。需要有效的 OAuth2.0 認證。

- `GET /users/search?email={email}`
  根據 Email 查詢用戶資訊。

- `PUT /users/update`
  根據 Email 更新用戶角色。請求體為 `RoleDTO`。

- `DELETE /users/{userId}/delete`
  根據用戶 ID 刪除帳號。需要有效的 OAuth2.0 認證。

### Keycloak 相關 API

- `GET /keycloak/buildAuthUrl`
  生成 Keycloak 的授權網址。

- `POST /keycloak/getToken`
  使用授權碼 (code) 向 Keycloak 請求 Access Token 和 Refresh Token。請求體為 `CodeDTO`。

- `POST /keycloak/exchangeAccessToken`
  使用 Refresh Token 向 Keycloak 換取新的 Access Token。請求體為 `RefreshTokenDTO`。
