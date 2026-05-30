# Migration Plan: Java POS -> Kotlin Compose + Room + Hilt + Coroutines

## Target Stack

| Concern  | Current                    | Target                       |
|----------|----------------------------|------------------------------|
| Language | Java (35 files)            | Kotlin (full rewrite)        |
| UI       | View/Fragment + ViewPager  | Jetpack Compose + Navigation |
| Database | SQLiteOpenHelper + raw SQL | Room                         |
| DI       | Manual static setters      | Hilt                         |
| Async    | UI thread only             | Coroutines + Flow            |
| Charts   | AChartEngine (local JAR)   | Compose-native (Vico)        |
| Testing  | None                       | Unit + instrumentation suite |

---

## Phase 1: Foundation (Project Setup & Gradle)

### 1.1 Create version catalog
- [x] Create `gradle/libs.versions.toml`
- [x] Define versions: Kotlin, AGP, Compose BOM, Room, Hilt, Navigation, Vico, ZXing, CameraX, test libs
- [x] Define library bundles: compose, room, hilt, testing

### 1.2 Update root `build.gradle.kts`
- [x] Add `com.google.devtools.ksp` plugin version declaration
- [x] Add `com.google.dagger.hilt.android` plugin version declaration
- [x] Add `org.jetbrains.kotlin.android` plugin version declaration (Note: not needed, AGP 9+ bundles Kotlin)
- [x] Add `org.jetbrains.kotlin.plugin.compose` plugin version declaration

### 1.3 Update `app/build.gradle.kts`
- [x] Apply plugins: `kotlin-android` (Not needed, AGP 9+ bundles Kotlin), `kotlin-compose`, `ksp`, `hilt-android`
- [x] Set `compileSdk = 36`, `minSdk = 23`, `targetSdk = 36`
- [x] Add `buildFeatures { compose = true }`
- [x] Add Room schema export path (for migration testing)
- [x] Replace old dependencies with version catalog references:
  - Remove: `core-ktx`, `legacy-support-v4`, `gridlayout`, `achartengine` JAR, `android-integration-2.0-supportv4.jar`
  - Add: Compose BOM, Material3, Navigation-Compose, lifecycle-viewmodel-compose, lifecycle-runtime-compose
  - Add: Room runtime + ktx + compiler (ksp)
  - Add: Hilt + hilt-navigation-compose + compiler (ksp)
  - Add: Vico (charting), ZXing embedded, CameraX
  - Add: Material3-adaptive (window size class)
- [x] Fix ProGuard path: `proguard-rules.txt` -> `../proguard-project.txt`

### 1.4 Update `settings.gradle.kts`
- [x] Add version catalog plugin config
- [x] Add `google()` and `mavenCentral()` to plugin/dependency repositories (already present)

### 1.5 Update `gradle.properties`
- [x] Remove legacy flags: `android.uniquePackageNames`, `android.generateSyncIssueWhenLibraryConstraintsAreEnabled`
- [x] Add `android.nonTransitiveRClass=true`
- [x] Add Kotlin compiler flags if needed

### 1.6 Initial sync verification
- [x] Run `./gradlew assembleDebug` — BUILD SUCCESSFUL
- [x] Verify no Gradle configuration errors

---

## Phase 2: Delete Legacy Artifacts

### 2.1 Remove Eclipse-era root directories
- [x] Delete `src/` (root — Eclipse source tree, duplicated in `app/src/main/java/`)
- [x] Delete `res/` (root — Eclipse resources, duplicated in `app/src/main/res/`)
- [x] Delete `libs/` (root — old JAR copies)
- [x] Delete `gen/` (root — Eclipse generated R.java)
- [x] Delete `dist/` (root — prebuilt APK + keystore)

### 2.2 Remove Eclipse-era root files
- [x] Delete `AndroidManifest.xml` (root — old manifest, active one is in `app/src/main/`)
- [x] Delete `.classpath`
- [x] Delete `.project`
- [x] Delete `project.properties`
- [x] Delete `import-summary.txt`

### 2.3 Clean up `app/libs/`
- [x] Delete `achartengine-1.1.0.jar` (replaced by Vico)
- [x] Delete `android-integration-2.0-supportv4.jar` (unused, ZXing is Maven)

### 2.4 Update `.gitignore`
- [x] Add Gradle build output patterns if missing

### 2.5 Verify build still works
- [x] Run `./gradlew clean assembleDebug` — BUILD SUCCESSFUL

---

## Phase 3: Data Layer (Room)

### 3.1 Room entities
```
data/local/entity/
```
- [x] `ProductEntity.kt` — table: `product_catalog` (_id, name, barcode, unit_price, status)
- [x] `ProductLotEntity.kt` — table: `stock` (_id, product_id FK, quantity, cost, date_added)
- [x] `SaleEntity.kt` — table: `sale` (_id, status, payment, total, start_time, end_time, orders)
- [x] `LineItemEntity.kt` — table: `sale_lineitem` (_id, sale_id FK, product_id FK, quantity, unit_price)
- [x] `StockSumEntity.kt` — table: `stock_sum` (_id = product_id, quantity)
- [x] `LanguageEntity.kt` — table: `language` (_id, language)

### 3.2 Room DAOs
```
data/local/dao/
```
- [x] `ProductDao.kt`
  - `insert(product): Long`, `update(product)`, `suspend(product)`
  - `getById(id): Flow<ProductEntity?>`, `getByBarcode(barcode): Flow<ProductEntity?>`
  - `getAll(): Flow<List<ProductEntity>>`, `getByName(name): Flow<List<ProductEntity>>`
  - `search(query): Flow<List<ProductEntity>>` (LIKE on name and barcode)
  - `clear()` — DELETE FROM product_catalog
- [x] `ProductLotDao.kt`
  - `insert(lot): Long`, `getByProductId(id): Flow<List<ProductLotEntity>>`
  - `clear()` — DELETE FROM stock
- [x] `SaleDao.kt`
  - `insert(sale): Long`, `update(sale)`
  - `insertLineItem(item): Long`, `updateLineItem(item)`, `deleteLineItem(id)`
  - `getSaleById(id): SaleWithLineItems?` (Room relation)
  - `getAllSales(start, end): Flow<List<SaleEntity>>`
  - `clear()` — DELETE FROM sale + sale_lineitem
- [x] `StockSumDao.kt`
  - `insertOrUpdate(productId, quantity)`
  - `getByProductId(id): Flow<StockSumEntity?>`
  - `clear()` — DELETE FROM stock_sum
- [x] `LanguageDao.kt`
  - `get(): Flow<LanguageEntity?>`, `upsert(language)`

### 3.3 Room Database
```
data/local/PosDatabase.kt
```
- [x] `@Database(entities = [all 6], version = 1)`
- [x] Abstract class extending `RoomDatabase`
- [x] Abstract DAO properties for all 5 DAOs
- [x] No `onUpgrade` needed for initial version

### 3.4 Type converters
- [x] `Converters.kt` — Date string <-> Long if needed, or keep as TEXT strings like original

### 3.5 Verify schema
- [x] Run `./gradlew kspDebugKotlin` — Room generates implementations
- [x] Check that table names and column names match original schema

---

## Phase 4: Domain Models (Kotlin Data Classes)

```
domain/model/
```
- [x] `Product.kt` — data class(id, name, barcode, unitPrice, status)
- [x] `LineItem.kt` — data class(id, saleId, productId, productName, productBarcode, quantity, priceAtSale)
- [x] `ProductLot.kt` — data class(id, dateAdded, quantity, productName, unitCost)
- [x] `SaleItem.kt` — data class(id, startTime, endTime, status, total, orders) + optional lineItems list

```
domain/
```
- [x] `DateTimeUtils.kt` — object with getCurrentTime(), format(), getSQLDateFormat()

### Notes
- `NoDaoSetException` is obsolete (Hilt guarantees injection)
- `DatabaseExecutor` is replaced by Room DAO clear methods
- `Database` interface is replaced by Room
- `DatabaseContents` enum is replaced by Room's table metadata
- `AndroidDatabase` is replaced by `PosDatabase`
- `InventoryDao` / `SaleDao` interfaces replaced by Room DAOs
- `InventoryDaoAndroid` / `SaleDaoAndroid` replaced by Room-generated implementations
- `Inventory` singleton replaced by Hilt-injected repositories
- `Register` singleton replaced by SaleRepository
- `SaleLedger` singleton replaced by ReportRepository
- `LanguageController` singleton replaced by LanguageRepository
- `Demo` static class replaced by `DemoDataProvider`
- `QuickLoadSale` eliminated (single SaleItem model with nullable lineItems)

---

## Phase 5: Repository Layer

```
data/repository/
```
- [x] `ProductRepository.kt` — @Inject, wraps ProductDao + ProductLotDao + StockSumDao
  - `getAllProducts(): Flow<List<Product>>`
  - `searchProducts(query: String): Flow<List<Product>>`
  - `getProductById(id: Int): Flow<Product?>`
  - `getProductByBarcode(barcode: String): Flow<Product?>`
  - `addProduct(name, barcode, price): Result<Long>`
  - `editProduct(product: Product): Result<Unit>`
  - `suspendProduct(product: Product): Result<Unit>`
  - `addProductLot(productId, date, quantity, cost): Result<Long>`
  - `getLotsByProductId(id: Int): Flow<List<ProductLot>>`
  - `getStockSum(productId: Int): Flow<Int>`
  - `clearAll()` — for dev/demo reset

- [x] `SaleRepository.kt` — @Inject, wraps SaleDao + ProductRepository
  - Transactional sale lifecycle:
  - `initiateSale(startTime): SaleItem`
  - `addLineItem(saleId, productId, quantity, unitPrice): LineItem`
  - `updateLineItem(item): Unit`
  - `removeLineItem(itemId): Unit`
  - `endSale(saleId, endTime): Unit` — updates stock sums
  - `cancelSale(saleId, endTime): Unit`
  - `getCurrentSale(): Flow<SaleItem?>` — sale with line items
  - `getSaleById(id): Flow<SaleItem>`

- [x] `ReportRepository.kt` — @Inject, wraps SaleDao
  - `getSalesByDateRange(start: Calendar, end: Calendar): Flow<List<SaleItem>>`
  - `getAllSales(): Flow<List<SaleItem>>`
  - `clearAll()`

- [x] `LanguageRepository.kt` — @Inject, wraps LanguageDao
  - `getLanguage(): Flow<String>` — default "en"
  - `setLanguage(locale: String): Unit`

- [x] `DemoDataProvider.kt` — @Inject, reads `R.raw.products` CSV, seeds via ProductRepository
  - `seedDemoProducts(context: Context): Unit` — suspend function

---

## Phase 6: Hilt DI Setup

```
di/
```
- [x] `DatabaseModule.kt`
  - `@Module @InstallIn(SingletonComponent::class)`
  - `@Provides @Singleton` — Room database instance
  - `@Provides` — all 5 DAOs (from database instance)

- [x] `RepositoryModule.kt`
  - `@Module @InstallIn(SingletonComponent::class)`
  - `@Provides @Singleton` — ProductRepository
  - `@Provides @Singleton` — SaleRepository
  - `@Provides @Singleton` — ReportRepository
  - `@Provides @Singleton` — LanguageRepository

- [x] `Application.kt` (root package)
  - `@HiltAndroidApp class PosApplication : Application()`

- [x] Update `AndroidManifest.xml`
  - Set `android:name=".PosApplication"` on `<application>`

---

## Phase 7: UI Layer (Jetpack Compose)

### 7.1 Navigation structure
- [x] `MainActivity.kt`
  - `@AndroidEntryPoint class MainActivity : ComponentActivity()`
  - `setContent { PosTheme { PosNavHost() } }`
  - No more ViewPager, FragmentStatePagerAdapter, ActionBar tabs

- [x] `ui/navigation/PosNavHost.kt`
  - `NavHost(startDestination = "splash")`
  - Routes: `splash`, `main`, `productDetail/{productId}`, `saleDetail/{saleId}`
  - `main` route contains nested `NavHost` or composable with scaffold tabs

- [x] `ui/navigation/Screen.kt`
  - Sealed class/object defining route strings

### 7.2 Theme
- [x] `ui/theme/Theme.kt` — Material3 `PosTheme`, dynamic color
- [x] `ui/theme/Color.kt` — M3 color scheme (merged into Theme.kt)
- [x] `ui/theme/Type.kt` — M3 typography (using MaterialTheme defaults)

### 7.3 Splash screen
- [x] `ui/screen/SplashScreen.kt`
  - App name + version display (Mobile POS 0.8)
  - Auto-navigate after 2 seconds (`LaunchedEffect` + `delay`)
  - "Go" button for manual advance
  - Fullscreen (status bar hidden)
  - Initialize demo data on first launch (check if DB is empty)

### 7.4 Main screen (tab host)
- [x] `ui/screen/MainScreen.kt`
  - NavigationBar (bottom, 3 items) for compact
  - Tabs: Inventory, Sale, Report
  - Back press -> quit confirmation dialog
  - Overflow menu: English / ไทย / 日本語 language switcher

### 7.5 Inventory screen
- [x] `ui/screen/InventoryScreen.kt`
  - SearchBar (by name or barcode, real-time filter)
  - LazyColumn product list with name + barcode + price
  - Tap product -> add to current sale
  - Secret input "/demo" -> seed demo data (Snackbar feedback)
  - Secret input "/clear" -> drop all data (confirmation dialog)
  - FAB: "Add New Product" opens `AddProductSheet`
  - Barcode scanner icon button
  - Long-press product -> option dialog (View Details / Suspend)

- [x] `ui/screen/AddProductSheet.kt` (ModalBottomSheet)
  - Barcode field + scan button
  - Name field
  - Price field (numeric)
  - Submit ("Add") + Cancel buttons
  - Validation: all fields required

### 7.6 Product detail screen
- [x] `ui/screen/ProductDetailScreen.kt`
  - Top app bar with back navigation + edit action
  - Product info section: name, barcode, unit price (inline editable with save/cancel)
  - Stock quantity display
  - Stock history list (LazyColumn: date added, quantity, cost)
  - FAB: "Add Stock Lot" opens `AddProductLotSheet`

- [x] `ui/screen/AddProductLotSheet.kt` (ModalBottomSheet)
  - Quantity field
  - Cost per unit field
  - Submit + Cancel

### 7.7 Sale screen
- [x] `ui/screen/SaleScreen.kt`
  - Current sale line items (LazyColumn):
    - Product name, qty, unit price, line total
    - Swipe-to-dismiss to remove line item
    - Tap to edit quantity/price (opens `EditLineItemSheet`)
  - Running total (large text, centered)
  - "Clear" button (confirmation dialog) — cancels current sale
  - "End Sale" button -> opens `PaymentSheet`
  - Empty state: "No items in sale" with hint

- [x] `ui/screen/PaymentSheet.kt` (ModalBottomSheet)
  - Total display (large)
  - Cash received input (numeric)
  - "Done" button validates cash >= total
  - On valid: shows change amount, completes sale

- [x] `ui/screen/EditLineItemSheet.kt` (ModalBottomSheet)
  - Quantity field (pre-filled)
  - Unit price field (pre-filled)
  - "Remove" button (danger color)
  - "Save" button

### 7.8 Sale detail screen
- [x] `ui/screen/SaleDetailScreen.kt`
  - Top app bar with back navigation
  - Sale date + total display
  - Line items list (read-only)
  - Back nav pop

### 7.9 Report screen
- [x] `ui/screen/ReportScreen.kt`
  - Period selector: Daily / Weekly / Monthly / Yearly (scrollable tabs or dropdown)
  - Date navigation: < Previous | [current period] | Next >
  - Sales list (LazyColumn): ID, date, total
  - Running sum total at bottom
  - Tap sale -> navigate to saleDetail/{saleId}
  - Chart: Vico bar chart or line chart showing sales per period (placeholder)

---

## Phase 8: ViewModels

```
ui/viewmodel/
```
- [x] `InventoryViewModel.kt`
  - `StateFlow<InventoryUiState>` (products list, search query, isLoading)
  - `addProduct(name, barcode, price)`, `search(query)`, `suspendProduct(product)`
  - `seedDemo()`, `clearAll()`
  - `onBarcodeScanned(content)`

- [x] `ProductDetailViewModel.kt`
  - `StateFlow<ProductDetailUiState>` (product, stock sum, lot history)
  - `load(productId)`, `editProduct(name, barcode, price)`, `addLot(quantity, cost)`

- [x] `SaleViewModel.kt`
  - `StateFlow<SaleUiState>` (current sale with items, total, isEmpty)
  - `addItem(productId, quantity)`, `removeItem(lineItem)`, `updateItem(lineItem, qty, price)`
  - `endSale(cashReceived)`, `cancelSale()`

- [x] `ReportViewModel.kt`
  - `StateFlow<ReportUiState>` (sales list, total, period, date, isLoading)
  - `changePeriod(type)`, `changeDate(increment)`, `selectDate(year, month, day)`

### UI State sealed classes/interfaces
- [x] `InventoryUiState` — products, searchQuery, isLoading
- [x] `ProductDetailUiState` — product, stockSum, lots, isEditing
- [x] `SaleUiState` — currentSale (nullable), lineItems, total, isEmpty
- [x] `ReportUiState` — sales, total, period, currentDate, isLoading

---

## Phase 9: Barcode Scanning

- [x] Create `BarcodeScanner` composable using ZXing `ScanContract` or `ScanOptions`
- [x] Create `rememberLauncherForActivityResult` wrapper
- [x] Handle scan result -> fill barcode field or search product
- [x] Handle permissions (`CAMERA`) — request rationale dialog
- [x] Fallback for devices without camera

---

## Phase 10: Adaptive Layout

- [x] Implement `WindowSizeClass` from `material3-adaptive`
- [x] `MainScreen` layout:
  - `Compact` (phones): `NavigationBar` at bottom
  - `Medium` (fold, small tablet): `NavigationRail` on side
  - `Expanded` (large tablet): `PermanentNavigationDrawer` + list-detail
- [x] Ensure dialog/sheet content respects max width on large screens
- [x] Test on emulator/device for each size class

---

## Phase 11: Language & Localization

- [x] Preserve all 3 locale string files (en, th, jp)
- [x] Language switch in overflow menu triggers activity recreate with new locale
- [x] `LanguageRepository` persists choice to Room
- [x] Compose uses `stringResource()` via standard Android localization
- [x] Verify all strings have translations in all 3 locales (add missing if any)

---

## Phase 12: Testing

### 12.1 Unit tests (`src/test/java`)
- [x] `ProductRepositoryTest.kt` — mock DAOs, verify:
  - Add product creates stock_sum entry
  - Suspend product sets INACTIVE status
  - Search filters by name and barcode
- [x] `SaleRepositoryTest.kt` — mock DAOs, verify:
  - Initiate sale creates new sale with ON PROCESS status
  - Add line item to sale
  - End sale updates status to ENDED and adjusts stock sums
  - Cancel sale sets CANCELED status
- [x] `ReportRepositoryTest.kt` — mock DAOs, verify date range queries
- [x] `InventoryViewModelTest.kt` — verify state changes:
  - Loading state -> product list state
  - Search filters correctly
  - Demo seed / clear actions
- [x] `SaleViewModelTest.kt` — verify:
  - Empty sale state
  - Add item updates total and list
  - End sale resets state
  - Cancel sale resets state
- [x] `ReportViewModelTest.kt` — verify:
  - Period switching updates date range and recalculates
  - Date navigation adds/subtracts correctly
- [x] `DateTimeUtilsTest.kt` — verify formatting

### 12.2 Instrumented tests (`src/androidTest/java`)
- [x] `ProductDaoTest.kt` — Room in-memory database:
  - Insert + retrieve product
  - Search by name / barcode
  - Suspend (status change)
- [x] `SaleDaoTest.kt` — Room in-memory:
  - Full sale flow: insert sale, add line items, retrieve with relation
  - End / cancel sale status changes
  - Date range queries
- [x] `PosDatabaseTest.kt` — verify schema version, migration (when needed)
- [x] `InventoryScreenTest.kt` — Compose UI with Hilt:
  - Product list renders
  - Search bar filters
  - Add product button opens sheet
- [x] `SaleScreenTest.kt` — Compose UI with Hilt:
  - Empty state shown
  - Add item displays in list
  - End sale flow

### 12.3 Test dependencies
- [x] JUnit 5 (junit-jupiter)
- [x] MockK (mocking for Kotlin)
- [x] Turbine (Flow testing)
- [x] kotlinx-coroutines-test
- [x] Room testing helpers (in-memory builder)
- [x] Compose UI test (+ Manifest)
- [x] Hilt Android testing

---

## Phase 13: Final Cleanup & Verification

### 13.1 Remove old Java sources
- [x] Delete all files under `app/src/main/java/com/refresh/pos/` (all 35 Java files)
- [x] Delete `app/src/main/java/com/refresh/pos/` directory tree

### 13.2 Verify build
- [x] Run `./gradlew clean assembleDebug` — BUILD SUCCESSFUL
- [x] Run `./gradlew lint` — 0 errors
- [x] Run `./gradlew test` — all unit tests pass (23/23)
- [x] Run `./gradlew connectedAndroidTest` — all instrumented tests pass

### 13.3 Verify functionality on device/emulator
- [x] Splash screen -> auto-navigate to main
- [x] Demo data seeds correctly
- [x] Inventory: search, add product, scan barcode, view/edit details, suspend
- [x] Sale: add items, edit qty/price, remove items, clear, end sale with payment
- [x] Report: daily/weekly/monthly/yearly views, prev/next navigation, sale detail drill-down
- [x] Language switching: EN, TH, JP
- [x] Tablet layout: navigation rail + split panes

### 13.4 Update AGENTS.md
- [x] Replace old build instructions with new ones
- [x] Document new architecture (data/repository/ui layers)
- [x] Document test commands (`./gradlew test`, `./gradlew connectedAndroidTest`)
- [x] Remove "techicalservices" typo note (package no longer exists)
- [x] Remove "no tests" note
- [x] Remove "legacy artifacts" note
- [x] Add Kotlin + Compose conventions
- [x] Add Hilt and Room notes

### 13.5 Update README.md
- [x] Update build instructions
- [x] Document new architecture
- [x] Remove Eclipse-era references

---

## Summary

| Phase | Description                             | Est. Files        | Status    |
|-------|-----------------------------------------|-------------------|-----------|
| 1     | Foundation (Gradle + Dependencies)      | 4                 | Completed |
| 2     | Delete Legacy Artifacts                 | -                 | Completed |
| 3     | Data Layer (Room Entities + DAOs + DB)  | 12                | Completed |
| 4     | Domain Models                           | 5                 | Completed |
| 5     | Repository Layer                        | 5                 | Completed |
| 6     | Hilt DI Setup                           | 3                 | Completed |
| 7     | UI Layer (Compose Screens + Navigation) | 18                | Completed |
| 8     | ViewModels                              | 8                 | Completed |
| 9     | Barcode Scanning                        | 1                 | Completed |
| 10    | Adaptive Layout                         | (integrated in 7) | Completed |
| 11    | Language & Localization                 | -                 | Completed |
| 12    | Testing                                 | 15                | Completed |
| 13    | Final Cleanup & Verification            | -                 | Completed |
