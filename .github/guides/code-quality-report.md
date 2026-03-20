## Code Quality Report: OrderService

### Findings

| # | Smell | Location | Evidence | Impact | Remediation |
|---|-------|----------|----------|--------|-------------|
| 1 | **Primitive Obsession** | `placeOrder()`, `addItemsToOrder()` | `List<Map<String, Object>> items` parameter — untyped Map with string keys, Object values | Requires extensive type casting (lines 82–91), null checks scattered throughout, no IDE autocomplete, hard to document expected schema | Create a `CreateOrderItemRequest` DTO with typed fields (`String productId`, `String productName`, `int quantity`, `BigDecimal price`) and use constructor injection or bean validation |
| 2 | **Long Method** | `addItemsToOrder()` lines 71–95 | ~25 lines mixing: Map extraction, null validation, type casting, NumberFormatException handling, object construction | Low cohesion; method has 3 distinct concerns (extraction, validation, conversion); difficult to unit test each concern in isolation; hard to reuse validation | Extract `private OrderItem buildOrderItemFromMap(Map<String, Object> item)` for construction, move validation to `CreateOrderItemRequest` class with `@Valid` annotations |
| 3 | **Inappropriate Intimacy** | `addItemsToOrder()` line 94 | `order.getItems().add(orderItem)` — directly mutating Order's internal collection | Bypasses Order's encapsulation; `addItem(OrderItem)` method exists (Order.java line 63) but ignored; future changes to Order's collection logic won't be reflected here | Replace with `order.addItem(orderItem)` to use the domain method; establishes single point of change |
| 4 | **Unchecked Type Casting** | `addItemsToOrder()` lines 82–91 | `(Integer) qtyObj` and implicit casting in `new BigDecimal(priceObj.toString())` without type validation | `ClassCastException` or `NumberFormatException` thrown at runtime instead of `InvalidOrderException`; clients get 500 errors instead of 400 Bad Request | Add explicit type checks: `if (!(qtyObj instanceof Integer))` before cast; or use DTO deserialization which validates automatically |
| 5 | **Data Clumps** | `calculateOrderTotal()` lines 97–102 | Loop over `OrderItem` list, accessing `item.getPrice()` and `item.getQty()` repeatedly across multiple methods | Same pattern appears in `placeOrder()` (line 43) and `recalculateTotal()` (line 160); duplicated responsibility | Extract a method on Order domain: `public BigDecimal calculateTotal()` that encapsulates this logic; service just calls `order.calculateTotal()` |
| 6 | **Feature Envy** | `addItemsToOrder()` lines 71–95 | Method spends 25 lines extracting, validating, and building Map data; should delegate more to domain objects or a dedicated converter | Violates SRP; service mixes infrastructure concerns (Map parsing) with business logic (OrderItem construction); hard to test independently | Create `OrderItemFactory` or use mapstruct/ModelMapper; let OrderItem handle its own validation logic |
| 7 | **Divergent Change** (Minor) | `OrderService` class | Six public methods each addressing different responsibilities: order creation, retrieval, cancellation, recalculation | Class could be split if order query/retrieval logic grows; currently manageable but mixing write (placeOrder, cancelOrder) and read (getOrder, getOrdersByCustomer) | Monitor for growth; consider extracting read operations to `OrderQueryService` if complexity increases |
| 8 | **Speculative Generality** (Minor) | `addItemsToOrder()` parameter type | Using generic `Map<String, Object>` to future-proof against unknown item types instead of defining a concrete contract | Makes code harder to understand without providing actual flexibility; clients still must know the exact key names | Define concrete DTO; if flexibility is truly needed, use `@JsonAnySetter` in DTO to capture unknown fields |

---

### Code Smell Clusters

**High-Priority Cluster**: Primitive Obsession + Long Method + Inappropriate Intimacy
- Root cause: Using raw `Map<String, Object>` for API input instead of a typed DTO
- Cascading effect: Requires defensive coding (null checks, type casts), bypasses encapsulation, duplicates validation logic
- Single fix: Introduce `CreateOrderItemRequest` DTO → eliminates 80% of the issues above

**Secondary Cluster**: Data Clumps + Feature Envy
- Root cause: Business logic (total calculation) scattered across multiple methods
- Fix: Move `calculateOrderTotal()` to Order domain method; refactor `addItemsToOrder()` logic into OrderItem constructor or factory

---

### Quality Score

**Smells found:** 8 (4 major, 2 minor, 2 informational)

**Estimated refactoring effort:** Medium (4–6 hours)
- Replace `Map<String, Object>` with DTO: 1–2 hours
- Extract `addItem()` usage and total calculation: 1–2 hours
- Add type validation / exception handling: 1 hour
- Update tests: 1 hour

**Priority recommendation (in order of impact):**
1. **Replace `Map<String, Object>` with `CreateOrderItemRequest` DTO** — eliminates Primitive Obsession and unvalidated type casting; improves API contract clarity
2. **Use `order.addItem()` instead of direct list mutation** — restores encapsulation; single point of change for Order invariants
3. **Extract total calculation to Order domain** — eliminates data clumps; simplifies service; easier to test

---

### Health Check Summary

| Dimension | Status | Notes |
|-----------|--------|-------|
| Encapsulation | ⚠️ Warning | Order's `addItem()` method ignored; collections exposed |
| Type Safety | ⚠️ Warning | Unchecked casts; untyped Map inputs |
| Single Responsibility | ✅ Good | Service layer focused on orchestration, not mixed concerns |
| Testability | ⚠️ Warning | Long method with side effects hard to unit test |
| Exception Handling | ✅ Good | Custom exceptions, not swallowed |
| API Design | ⚠️ Warning | Using Map instead of DTO; no bean validation |

