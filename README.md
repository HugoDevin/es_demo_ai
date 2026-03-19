# E-commerce Demo Backend

這是一個以 **Spring Boot 3** 建置的電商平台後端示範專案，實作了 Phase 1 所需的核心能力：

- JWT 驗證與登入 / 登出 / 註冊
- 商品列表、關鍵字查詢、單筆詳情
- Seller 商品管理（新增 / 修改 / 上下架 / soft delete）
- Buyer 建立訂單、查詢我的訂單、取消訂單
- Seller 更新訂單狀態
- H2 記憶體資料庫
- `PaymentGateway` 介面與 `CashOnDeliveryImpl` 實作

---

## 技術棧

- Java 17
- Spring Boot 3.3.4
- Spring Web
- Spring Data JPA
- Spring Security
- H2 Database
- JJWT
- Lombok
- Maven

---

## 專案結構

```text
src/main/java/com/example/ecommerce
├── config
├── controller
├── dto
│   ├── request
│   └── response
├── exception
├── model
├── payment
│   └── impl
├── repository
├── security
└── service
    └── impl
```

---

## 主要功能

### 1. 認證與授權

- `POST /api/v1/auth/register`：註冊
- `POST /api/v1/auth/login`：登入並取得 JWT
- `POST /api/v1/auth/logout`：登出並將 token 加入黑名單

角色：

- `BUYER`
- `SELLER`

---

### 2. 商品 API

- `GET /api/v1/products`
- `GET /api/v1/products/{id}`
- `POST /api/v1/products`
- `PUT /api/v1/products/{id}`
- `PATCH /api/v1/products/{id}/status`
- `DELETE /api/v1/products/{id}`

說明：

- 刪除採 **soft delete**，實際上是將商品狀態設為 `INACTIVE`
- 商品列表只回傳 `ACTIVE` 商品

---

### 3. 訂單 API

- `POST /api/v1/orders`
- `GET /api/v1/orders`
- `GET /api/v1/orders/{id}`
- `PATCH /api/v1/orders/{id}/status`
- `DELETE /api/v1/orders/{id}`

說明：

- 建立訂單時會寫入 `order_items.product_name` 與 `unit_price` 快照
- 建立訂單時使用 SQL 層原子扣庫存邏輯
- 付款流程目前支援 `CASH_ON_DELIVERY`

---

## 安全性設計

- 使用 Bearer Token（JWT）進行驗證
- Spring Security 依 API 路由限制角色
- 登出後 token 會被加入記憶體黑名單

Header 範例：

```http
Authorization: Bearer <JWT_TOKEN>
```

---

## 資料庫設定

專案使用 H2 in-memory database：

- JDBC URL: `jdbc:h2:mem:ecommerce;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- H2 Console: `/h2-console`

---

## 啟動方式

### 1. 編譯與測試

```bash
mvn test
```

### 2. 啟動應用程式

```bash
mvn spring-boot:run
```

啟動後預設網址：

- API: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`

---

## 已知注意事項

1. 本專案目前為示範用途，未加入正式環境所需的 refresh token / redis / DB migration。
2. `TokenBlacklistService` 目前使用記憶體儲存，重啟服務後黑名單資料不會保留。
3. 若使用某些 IDE 或自行覆寫 dependency 版本，需避免 Jackson 被舊版套件降版，否則可能出現：
   - `NoClassDefFoundError: com/fasterxml/jackson/databind/cfg/DatatypeFeature`
4. 在目前這個執行環境中，Maven 可能因外部 repository 存取限制而無法下載 plugin 或 dependency。

---

## 後續建議

- 補上 integration tests / controller tests
- 補上 seller 僅可存取自身商品 / 訂單的更完整限制
- 補上信用卡付款流程（Phase 3）
- 將 JWT 黑名單改為 Redis
- 補上 Swagger / OpenAPI 文件

