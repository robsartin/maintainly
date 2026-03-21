# Maintainly — Class Diagrams

Separated into printable sections. Each diagram renders independently on GitHub.

---

## 1. Domain Model — Entity Hierarchy

```mermaid
classDiagram
    direction TB

    class BaseEntity {
        <<abstract>>
        -UUID id
        -Instant createdAt
        -Instant updatedAt
    }

    class OrgOwnedEntity {
        <<abstract>>
        -UUID organizationId
        +belongsTo(UUID) bool
    }

    class Organization {
        -String name
        -String logoUrl
        -byte[] profileImage
    }

    class AppUser {
        -String username
        -AppRole role
        -byte[] profileImage
    }

    class Item {
        -String name
        -String location
        -String manufacturer
        -String modelName
        -String serialNumber
        -Integer modelYear
        -String category
        -LocalDate purchaseDate
        -UUID facilityId
    }

    class Facility {
        -String name
        -String addressLine1
        -String city
        -String stateProvince
        -boolean active
    }

    class Vendor {
        -String name
        -String phone
        -String email
        -String website
        -boolean systemManaged
    }

    class VendorAltPhone {
        -String phone
        -String label
    }

    class ServiceSchedule {
        -String serviceType
        -FrequencyUnit frequencyUnit
        -int frequencyInterval
        -LocalDate nextDueDate
        -LocalDate lastCompletedDate
        -boolean active
    }

    class ServiceRecord {
        -String serviceType
        -LocalDate serviceDate
        -String summary
        -BigDecimal cost
        -String technicianName
    }

    class UserGroup {
        -String name
        -AppRole role
        -String description
    }

    class UserGroupMembership {
        -AppUser user
        -UserGroup group
    }

    class AuditEntry {
        -String username
        -String entityType
        -UUID entityId
        -AuditAction action
        -Instant timestamp
    }

    BaseEntity <|-- OrgOwnedEntity
    BaseEntity <|-- Organization
    BaseEntity <|-- AppUser
    OrgOwnedEntity <|-- Item
    OrgOwnedEntity <|-- Facility
    OrgOwnedEntity <|-- Vendor
    OrgOwnedEntity <|-- VendorAltPhone
    OrgOwnedEntity <|-- ServiceSchedule
    OrgOwnedEntity <|-- ServiceRecord
    OrgOwnedEntity <|-- UserGroup
    OrgOwnedEntity <|-- AuditEntry
```

---

## 2. Domain Model — Entity Relationships

```mermaid
classDiagram
    direction LR

    class Organization
    class AppUser
    class Item
    class Facility
    class Vendor
    class VendorAltPhone
    class ServiceSchedule
    class ServiceRecord
    class UserGroup
    class UserGroupMembership

    AppUser "*" --> "0..1" Organization : belongs to
    Item "1" --> "*" ServiceSchedule : has schedules
    Item "1" --> "*" ServiceRecord : has records
    Vendor "1" --> "*" VendorAltPhone : has alt phones
    ServiceSchedule "*" --> "1" Item : scheduled for
    ServiceSchedule "*" --> "0..1" Vendor : preferred vendor
    ServiceRecord "*" --> "1" Item : recorded for
    ServiceRecord "*" --> "0..1" Vendor : performed by
    ServiceRecord "*" --> "0..1" ServiceSchedule : triggered by
    UserGroupMembership "*" --> "1" AppUser : member
    UserGroupMembership "*" --> "1" UserGroup : in group
```

---

## 3. Enums and Value Objects

```mermaid
classDiagram
    direction TB

    class FrequencyUnit {
        <<enum>>
        days
        weeks
        months
        years
        +advance(LocalDate, int) LocalDate
    }

    class AppRole {
        <<enum>>
        ADMIN
        FACILITY_MANAGER
        TECHNICIAN
        VIEWER
    }

    class AuditAction {
        <<enum>>
        CREATE
        UPDATE
        DELETE
        COMPLETE
        SKIP
    }

    class ItemSpec {
        <<record>>
        +String name
        +String location
        +String category
        +Integer modelYear
        +UUID facilityId
    }

    class VendorData {
        <<record>>
        +String name
        +String phone
        +String email
    }

    class FacilityData {
        <<record>>
        +String name
        +String addressLine1
        +String city
    }

    class ServiceCompletion {
        <<record>>
        +Vendor vendor
        +String summary
        +LocalDate serviceDate
        +BigDecimal cost
    }

    class PageRequest {
        <<record>>
        +int page
        +int size
        +String sort
        +String dir
    }

    class PageResult~T~ {
        <<record>>
        +List~T~ content
        +int page
        +int size
        +boolean hasNext
    }

    class ItemCostSummary {
        <<record>>
        +UUID itemId
        +String itemName
        +BigDecimal totalCost
    }

    class FacilitySummary {
        <<record>>
        +UUID facilityId
        +String facilityName
        +long itemCount
        +long overdueCount
    }

    class Validation {
        <<utility>>
        +requireNotBlank()$
        +requireMaxLength()$
        +trimOrNull()$
        +requireYearInRange()$
        +requireNonNegative()$
        +requireValidEmail()$
    }
```

---

## 4. Inbound Ports (Use Cases)

```mermaid
classDiagram
    direction TB

    class ItemManagement {
        <<interface>>
        +createItem(UUID, ItemSpec) Item
        +updateItem(UUID, UUID, ItemSpec) Item
        +deleteItem(UUID, UUID)
        +bulkDelete(UUID, List~UUID~)
        +bulkUpdateCategory(UUID, List~UUID~, String)
    }

    class ItemQuery {
        <<interface>>
        +findItems(UUID, String, String, PageRequest) PageResult
        +findByOrganization(UUID, PageRequest) PageResult
        +findDistinctCategories(UUID) List~String~
        +findByIdAndOrganization(UUID, UUID) Optional
    }

    class VendorManagement {
        <<interface>>
        +createVendor(UUID, VendorData) Vendor
        +updateVendor(UUID, UUID, VendorData) Vendor
        +deleteVendor(UUID, UUID)
    }

    class VendorQuery {
        <<interface>>
        +findAllVendors(UUID) List~Vendor~
        +findVendor(UUID, UUID) Optional
    }

    class ScheduleLifecycle {
        <<interface>>
        +createSchedule(...) ServiceSchedule
        +completeSchedule(UUID, UUID, ServiceCompletion) ServiceSchedule
        +skipSchedule(UUID, UUID) ServiceSchedule
        +deactivateSchedule(UUID, UUID)
    }

    class ScheduleQuery {
        <<interface>>
        +findActiveByOrganization(UUID, int, int) PageResult
        +findAllActiveByOrganization(UUID) List
    }

    class RecordCreation {
        <<interface>>
        +createRecord(UUID, Item, ServiceSchedule, ServiceCompletion)
    }

    class RecordManagement {
        <<interface>>
        +updateRecord(...) ServiceRecord
        +deleteRecord(UUID, UUID)
    }

    class FacilityManagement {
        <<interface>>
        +createFacility(UUID, FacilityData) Facility
        +updateFacility(UUID, UUID, FacilityData) Facility
        +deleteFacility(UUID, UUID)
    }

    class FacilityQuery {
        <<interface>>
        +findAllFacilities(UUID) List~Facility~
        +findFacility(UUID, UUID) Optional
    }

    class DashboardQuery {
        <<interface>>
        +countOverdueSchedules(UUID, LocalDate) long
        +countDueSoonSchedules(UUID, LocalDate, LocalDate) long
        +countItems(UUID) long
        +findRecentRecords(UUID, int) List
    }

    class CostQuery {
        <<interface>>
        +totalSpendForYear(UUID, int) BigDecimal
        +totalSpendAllTime(UUID) BigDecimal
        +topItemsByCost(UUID, int) List
    }

    class AuditLog {
        <<interface>>
        +log(UUID, String, String, UUID, String, AuditAction, String)
        +findRecentByOrganization(UUID, int) List
        +findByEntityId(UUID) List
    }

    class GroupManagement {
        <<interface>>
        +createGroup(UUID, String, AppRole, String) UserGroup
        +deleteGroup(UUID, UUID)
        +addMember(UUID, UUID, UUID)
        +removeMember(UUID, UUID, UUID)
    }

    class GroupQuery {
        <<interface>>
        +findAllGroups(UUID) List~UserGroup~
    }

    class ProfileImageUpload {
        <<interface>>
        +saveOrganizationImage(UUID, byte[], String)
        +saveUserImage(UUID, byte[], String)
    }

    class UserResolver {
        <<interface>>
        +resolveOrCreate(String) AppUser
    }
```

---

## 5. Outbound Ports (Repositories)

```mermaid
classDiagram
    direction TB

    class ItemRepository {
        <<interface>>
        +findByOrganizationId(UUID, PageRequest) PageResult
        +searchByOrganizationId(UUID, String, PageRequest) PageResult
        +findByIdAndOrganizationId(UUID, UUID) Optional
        +findDistinctCategoriesByOrganizationId(UUID) List
        +save(Item) Item
        +deleteByIdAndOrganizationId(UUID, UUID)
    }

    class VendorRepository {
        <<interface>>
        +findByOrganizationId(UUID) List
        +findByIdAndOrganizationId(UUID, UUID) Optional
        +save(Vendor) Vendor
        +deleteByIdAndOrganizationId(UUID, UUID)
    }

    class ServiceScheduleRepository {
        <<interface>>
        +findActiveByOrganizationId(UUID) List
        +findByIdAndOrganizationId(UUID, UUID) Optional
        +countActiveBeforeDate(UUID, LocalDate) long
        +countActiveBetweenDates(UUID, LocalDate, LocalDate) long
        +save(ServiceSchedule) ServiceSchedule
    }

    class ServiceRecordRepository {
        <<interface>>
        +findByItemIdAndOrganizationId(UUID, UUID) List
        +findRecentByOrganizationId(UUID, int) List
        +save(ServiceRecord) ServiceRecord
        +deleteByIdAndOrganizationId(UUID, UUID)
        +sumCostByOrganization(UUID) BigDecimal
    }

    class FacilityRepository {
        <<interface>>
        +findByOrganizationId(UUID) List
        +findByIdAndOrganizationId(UUID, UUID) Optional
        +save(Facility) Facility
        +deleteByIdAndOrganizationId(UUID, UUID)
    }

    class AuditEntryRepository {
        <<interface>>
        +save(AuditEntry) AuditEntry
        +findRecentByOrganizationId(UUID, int) List
        +findByEntityId(UUID) List
    }

    class AppUserRepository {
        <<interface>>
        +findByUsername(String) Optional
        +save(AppUser) AppUser
    }

    class OrganizationRepository {
        <<interface>>
        +findById(UUID) Optional
        +save(Organization) Organization
    }

    class UserGroupRepository {
        <<interface>>
        +findByOrganizationId(UUID) List
        +save(UserGroup) UserGroup
    }

    class UserGroupMembershipRepository {
        <<interface>>
        +findByGroupId(UUID) List
        +save(UserGroupMembership)
    }
```

---

## 6. Domain Services — Port Implementation

```mermaid
classDiagram
    direction LR

    class ItemManagementService {
        <<service>>
    }
    class ItemQueryService {
        <<service>>
    }
    class VendorManagementService {
        <<service>>
    }
    class ScheduleLifecycleService {
        <<service>>
    }
    class ServiceRecordService {
        <<service>>
    }
    class FacilityManagementService {
        <<service>>
    }
    class DashboardQueryService {
        <<service>>
    }
    class CostQueryService {
        <<service>>
    }
    class ScheduleQueryService {
        <<service>>
    }
    class AuditLogService {
        <<service>>
    }
    class GroupManagementService {
        <<service>>
    }

    class ItemManagement { <<interface>> }
    class ItemQuery { <<interface>> }
    class VendorManagement { <<interface>> }
    class VendorQuery { <<interface>> }
    class ScheduleLifecycle { <<interface>> }
    class RecordCreation { <<interface>> }
    class RecordManagement { <<interface>> }
    class FacilityManagement { <<interface>> }
    class FacilityQuery { <<interface>> }
    class DashboardQuery { <<interface>> }
    class CostQuery { <<interface>> }
    class ScheduleQuery { <<interface>> }
    class AuditLog { <<interface>> }
    class GroupManagement { <<interface>> }
    class GroupQuery { <<interface>> }

    ItemManagementService ..|> ItemManagement
    ItemQueryService ..|> ItemQuery
    VendorManagementService ..|> VendorManagement
    VendorManagementService ..|> VendorQuery
    ScheduleLifecycleService ..|> ScheduleLifecycle
    ServiceRecordService ..|> RecordCreation
    ServiceRecordService ..|> RecordManagement
    FacilityManagementService ..|> FacilityManagement
    FacilityManagementService ..|> FacilityQuery
    DashboardQueryService ..|> DashboardQuery
    CostQueryService ..|> CostQuery
    ScheduleQueryService ..|> ScheduleQuery
    AuditLogService ..|> AuditLog
    GroupManagementService ..|> GroupManagement
    GroupManagementService ..|> GroupQuery
```

---

## 7. Domain Services — Repository Dependencies

```mermaid
classDiagram
    direction LR

    class ItemManagementService { <<service>> }
    class ItemQueryService { <<service>> }
    class VendorManagementService { <<service>> }
    class ScheduleLifecycleService { <<service>> }
    class ServiceRecordService { <<service>> }
    class FacilityManagementService { <<service>> }
    class DashboardQueryService { <<service>> }
    class CostQueryService { <<service>> }
    class ScheduleQueryService { <<service>> }
    class AuditLogService { <<service>> }
    class GroupManagementService { <<service>> }

    class ItemRepository { <<interface>> }
    class VendorRepository { <<interface>> }
    class ServiceScheduleRepository { <<interface>> }
    class ServiceRecordRepository { <<interface>> }
    class FacilityRepository { <<interface>> }
    class AuditEntryRepository { <<interface>> }
    class UserGroupRepository { <<interface>> }
    class UserGroupMembershipRepository { <<interface>> }
    class RecordCreation { <<interface>> }

    ItemManagementService --> ItemRepository
    ItemQueryService --> ItemRepository
    ItemQueryService --> ServiceRecordRepository
    ItemQueryService --> ServiceScheduleRepository
    VendorManagementService --> VendorRepository
    ScheduleLifecycleService --> ServiceScheduleRepository
    ScheduleLifecycleService --> RecordCreation
    ServiceRecordService --> ServiceRecordRepository
    FacilityManagementService --> FacilityRepository
    DashboardQueryService --> ServiceScheduleRepository
    DashboardQueryService --> ItemRepository
    DashboardQueryService --> ServiceRecordRepository
    CostQueryService --> ServiceRecordRepository
    ScheduleQueryService --> ServiceScheduleRepository
    AuditLogService --> AuditEntryRepository
    GroupManagementService --> UserGroupRepository
    GroupManagementService --> UserGroupMembershipRepository
```

---

## 8. Application Layer — Controllers and Port Dependencies

```mermaid
classDiagram
    direction LR

    class ItemController {
        <<controller>>
        GET POST PUT DELETE /items
    }
    class VendorController {
        <<controller>>
        GET POST PUT DELETE /vendors
    }
    class ScheduleController {
        <<controller>>
        GET POST DELETE /schedules
    }
    class DashboardController {
        <<controller>>
        GET /
    }
    class ReportController {
        <<controller>>
        GET /reports
    }
    class FacilityController {
        <<controller>>
        GET POST PUT DELETE /facilities
    }
    class GroupController {
        <<controller>>
        GET POST PUT DELETE /settings/groups
    }
    class ActivityController {
        <<controller>>
        GET /activity
    }

    class ItemManagement { <<interface>> }
    class ItemQuery { <<interface>> }
    class VendorManagement { <<interface>> }
    class VendorQuery { <<interface>> }
    class ScheduleLifecycle { <<interface>> }
    class ScheduleQuery { <<interface>> }
    class RecordCreation { <<interface>> }
    class RecordManagement { <<interface>> }
    class FacilityManagement { <<interface>> }
    class FacilityQuery { <<interface>> }
    class DashboardQuery { <<interface>> }
    class CostQuery { <<interface>> }
    class AuditLog { <<interface>> }
    class GroupManagement { <<interface>> }
    class GroupQuery { <<interface>> }

    ItemController --> ItemManagement
    ItemController --> ItemQuery
    ItemController --> RecordCreation
    ItemController --> RecordManagement
    ItemController --> ScheduleLifecycle
    ItemController --> VendorQuery
    ItemController --> FacilityQuery
    ItemController --> AuditLog
    VendorController --> VendorManagement
    VendorController --> VendorQuery
    VendorController --> AuditLog
    ScheduleController --> ScheduleLifecycle
    ScheduleController --> ScheduleQuery
    ScheduleController --> AuditLog
    DashboardController --> DashboardQuery
    DashboardController --> FacilityQuery
    DashboardController --> AuditLog
    ReportController --> ItemQuery
    ReportController --> ScheduleQuery
    ReportController --> CostQuery
    FacilityController --> FacilityManagement
    FacilityController --> FacilityQuery
    GroupController --> GroupManagement
    GroupController --> GroupQuery
    ActivityController --> AuditLog
```

---

## 9. REST API Controllers

```mermaid
classDiagram
    direction LR

    class ItemApiController {
        <<restcontroller>>
        GET /api/v1/items
        POST /api/v1/items
        PUT /api/v1/items/id
        DELETE /api/v1/items/id
    }
    class VendorApiController {
        <<restcontroller>>
        GET /api/v1/vendors
        POST /api/v1/vendors
    }
    class ScheduleApiController {
        <<restcontroller>>
        GET /api/v1/schedules
    }
    class AuthController {
        <<restcontroller>>
        POST /api/auth/token
    }

    class ItemManagement { <<interface>> }
    class ItemQuery { <<interface>> }
    class VendorManagement { <<interface>> }
    class VendorQuery { <<interface>> }
    class ScheduleQuery { <<interface>> }
    class ApiTokenService { <<interface>> }
    class UserResolver { <<interface>> }

    ItemApiController --> ItemManagement
    ItemApiController --> ItemQuery
    ItemApiController --> UserResolver
    VendorApiController --> VendorManagement
    VendorApiController --> VendorQuery
    VendorApiController --> UserResolver
    ScheduleApiController --> ScheduleQuery
    ScheduleApiController --> UserResolver
    AuthController --> ApiTokenService
```

---

## 10. Infrastructure — JPA Adapters

```mermaid
classDiagram
    direction LR

    class JpaItemRepositoryAdapter { <<repository>> }
    class JpaFacilityRepositoryAdapter { <<repository>> }
    class JpaScheduleRepositoryAdapter { <<repository>> }
    class JpaServiceRecordRepository { <<repository>> }
    class JpaVendorRepository { <<repository>> }
    class JpaAppUserRepository { <<repository>> }
    class JpaOrganizationRepository { <<repository>> }
    class JpaAuditEntryRepository { <<repository>> }
    class JpaUserGroupRepository { <<repository>> }
    class JpaUserGroupMembershipRepository { <<repository>> }

    class ItemRepository { <<interface>> }
    class FacilityRepository { <<interface>> }
    class ServiceScheduleRepository { <<interface>> }
    class ServiceRecordRepository { <<interface>> }
    class VendorRepository { <<interface>> }
    class AppUserRepository { <<interface>> }
    class OrganizationRepository { <<interface>> }
    class AuditEntryRepository { <<interface>> }
    class UserGroupRepository { <<interface>> }
    class UserGroupMembershipRepository { <<interface>> }

    JpaItemRepositoryAdapter ..|> ItemRepository
    JpaFacilityRepositoryAdapter ..|> FacilityRepository
    JpaScheduleRepositoryAdapter ..|> ServiceScheduleRepository
    JpaServiceRecordRepository ..|> ServiceRecordRepository
    JpaVendorRepository ..|> VendorRepository
    JpaAppUserRepository ..|> AppUserRepository
    JpaOrganizationRepository ..|> OrganizationRepository
    JpaAuditEntryRepository ..|> AuditEntryRepository
    JpaUserGroupRepository ..|> UserGroupRepository
    JpaUserGroupMembershipRepository ..|> UserGroupMembershipRepository

    class SpringDataItemRepository { <<spring-data>> }
    class SpringDataFacilityRepository { <<spring-data>> }
    class SpringDataScheduleRepository { <<spring-data>> }
    class PageResultConverter { <<utility>> }

    JpaItemRepositoryAdapter --> SpringDataItemRepository
    JpaItemRepositoryAdapter --> PageResultConverter
    JpaFacilityRepositoryAdapter --> SpringDataFacilityRepository
    JpaScheduleRepositoryAdapter --> SpringDataScheduleRepository
    JpaScheduleRepositoryAdapter --> PageResultConverter
```
