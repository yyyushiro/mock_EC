# Spring Boot API

## API Reference

### Auth — `/api/auth`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register a new user. Sets `access_token` (15 min) and `refresh_token` (7 days) cookies. | None |
| POST | `/api/auth/login` | Log in with credentials. Sets `access_token` and `refresh_token` cookies. | None |
| POST | `/api/auth/refresh` | Rotate tokens using the `refresh_token` cookie. | Cookie: `refresh_token` |
| POST | `/api/auth/logout` | Invalidate the refresh token and clear both cookies. | Cookie: `refresh_token` |

---

### Customer — Products — `/api/customer/products`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/customer/products/{id}` | Get a single product by numeric ID. | JWT |
| GET | `/api/customer/products/search/{name}` | Search products by name. | JWT |
| GET | `/api/customer/products/search/price-range?minPrice=&maxPrice=` | Search products within a price range (both params optional). | JWT |

---

### Customer — Cart — `/api/customer/cart`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/customer/cart` | Add a product to the authenticated user's cart. Body: `{ productId, quantity }`. | JWT |
| GET | `/api/customer/cart` | Get the authenticated user's cart contents. | JWT |

---

### Customer — Orders — `/api/customer/orders`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/customer/orders` | Checkout: convert the authenticated user's cart into an order. | JWT |

---

### Seller — Products — `/api/seller/products`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/seller/products/{uuidV4}` | Get a single product by UUID. | JWT |
| POST | `/api/seller/products/` | Create a new product. | JWT |
| PUT | `/api/seller/products/{uuidV4}` | Update an existing product by UUID. | JWT |
| DELETE | `/api/seller/products/{uuidV4}` | Delete a product by UUID. | JWT |

---

## Notes

- Authentication uses **HttpOnly cookies** (`access_token`, `refresh_token`). JWT is verified on every protected request.
- Customer-facing product endpoints expose a numeric `id`; seller-facing endpoints use a `uuidV4` to hide internal IDs.
- The H2 console is available at `/h2-console` (development only).
