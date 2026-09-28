# BloodLink — Blood Donor Registry and Search

## 1. Project Overview

BloodLink is a REST API for registering blood donors, finding eligible donors by blood group and city, recording donations, viewing donation history, and viewing donor counts by blood group. It uses Spring Boot, Spring Data JPA, and MySQL.

## 2. Problem Statement

Finding a donor requires matching both the needed blood group and location while avoiding donors who have donated recently. BloodLink supports these searches and applies a 90-day eligibility cooldown based on a donor's last donation date.

## 3. Objectives

- Register and maintain donor details, including blood group, city, phone, and donation information.
- Search donors by blood group, optionally narrowed by city.
- Exclude donors who are still within the 90-day cooldown from eligible search results.
- Record donations and update the donor's last donation date.
- Provide a donor's donation history and donor counts by blood group.
- Validate API requests and return structured error responses.
- Expose REST endpoints documented with Swagger/OpenAPI.

## 4. Technologies Used

- Java 25
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- MySQL Connector/J
- Jakarta Bean Validation
- Maven and Maven Wrapper
- Springdoc OpenAPI 3.1.1 with Swagger UI
- Visual Studio Code for development (optional)

## 5. System Architecture

The application separates HTTP handling, business rules, persistence, and data representation:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
MySQL Database
```

- **Controllers** expose REST routes and map request/response DTOs.
- **Services** validate business rules, including blood-group support and the donor cooldown.
- **Repositories** use Spring Data JPA to query and persist entities.
- **DTOs** define API request and response shapes so JPA entities are not returned directly.
- **Entities** map donors, blood groups, and donation records to database tables.
- **Exception handling** converts known errors, validation failures, malformed request bodies, and unexpected failures to structured HTTP responses.

## 6. Main Modules

### Donor Management

Register, view, update, delete, and search donors. Search supports blood group and an optional city filter, and returns only eligible donors. A donor with donation history cannot be deleted, preserving the associated records.

### Donation Management

Record a donation for an existing donor, retrieve a donation record, and view a donor's history. Recording a donation updates `lastDonationDate` in the same transaction. The service rejects null or future dates and rejects donors who are still within the cooldown.

### Blood Group Management

The API lists the supported blood groups. Blood group rows are read from the database; the application does not automatically seed them at startup.

### Statistics

Return the donor count for each supported blood group. A supported group with no database row in `blood_groups` is reported with a count of zero.

### Validation and Exception Handling

Bean Validation checks request fields. The global exception handler returns 404 for missing resources, 400 for business-rule, validation, and malformed-body errors, and a generic 500 response for unexpected errors. Internal stack traces are not included in API responses.

## 7. 90-Day Eligibility Rule

A donor is eligible when `lastDonationDate` is null or at least 90 days have passed since that date:

```text
eligible = lastDonationDate is null
        OR days between lastDonationDate and today >= 90
```

Donors with fewer than 90 elapsed days are excluded from blood-group search results. The rule is implemented in the service layer, not in controllers or repository queries.

## 8. Supported Blood Groups

- A+
- A-
- B+
- B-
- AB+
- AB-
- O+
- O-

## 9. API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/donors` | Register a donor. |
| GET | `/api/donors` | List registered donors. |
| GET | `/api/donors/{id}` | Get a donor by ID. |
| PUT | `/api/donors/{id}` | Update name, phone, city, and blood group. The request cannot change `lastDonationDate`. |
| DELETE | `/api/donors/{id}` | Delete a donor if the donor has no donation history. |
| GET | `/api/donors/search?bloodGroup=O%2B` | Search eligible donors by blood group. |
| GET | `/api/donors/search?bloodGroup=O%2B&city=Chennai` | Search eligible donors by blood group and city. |
| GET | `/api/donors/statistics` | Get donor counts by supported blood group. |
| POST | `/api/donations` | Record a donation and update the donor's last donation date. |
| GET | `/api/donations/{id}` | Get a donation record by ID. |
| GET | `/api/donations/donor/{donorId}` | Get a donor's donation history, newest first. |
| GET | `/api/blood-groups` | List the supported blood groups. |

The `+` in blood-group query values must be URL-encoded as `%2B`.

## 10. Swagger / OpenAPI

When the application is running locally:

- Swagger UI: <http://localhost:8080/swagger-ui/index.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

Swagger UI can be used to inspect and try the documented REST APIs.

## 11. Database Design

Hibernate maps the following entities to MySQL tables:

- **BloodGroup** → `blood_groups`: generated ID and unique supported blood-group name.
- **Donor** → `donors`: name, phone, city, blood group reference, and nullable last donation date.
- **DonationRecord** → `donation_records`: donor reference and donation date.

Relationships:

- One `BloodGroup` can be associated with many `Donor` records. The donor table contains the `blood_group_id` foreign key.
- One `Donor` can have many `DonationRecord` records. The donation-record table contains the `donor_id` foreign key.

The development configuration uses Hibernate `update` to create or update mapped tables. It does not automatically create the eight blood-group rows. For a fresh database, create/use the `bloodlink` database, start the application once so Hibernate creates the tables, then insert the supported group rows once if they are not already present:

```sql
INSERT IGNORE INTO blood_groups (name) VALUES
('A+'), ('A-'), ('B+'), ('B-'),
('AB+'), ('AB-'), ('O+'), ('O-');
```

## 12. Validation and Error Handling

- `DonorRequest`: `name`, `phone`, `city`, and `bloodGroup` must not be blank. It does not contain `lastDonationDate`.
- `DonationRequest`: `donorId` must be present and positive; `donationDate` must be present.
- The service validates that a blood group is supported and rejects donation dates in the future or during a donor's cooldown.
- Errors use a JSON response containing a timestamp, HTTP status, message, and an `errors` map. Validation failures include field-level messages.

## 13. Project Structure

```text
src/
└── main/
    ├── java/com/bloodlink/
    │   ├── BloodLinkApplication.java
    │   ├── config/
    │   │   └── OpenApiConfig.java
    │   ├── controller/
    │   │   ├── BloodGroupController.java
    │   │   ├── DonationController.java
    │   │   └── DonorController.java
    │   ├── dto/
    │   │   ├── BloodGroupResponse.java
    │   │   ├── DonationRequest.java
    │   │   ├── DonationResponse.java
    │   │   ├── DonorRequest.java
    │   │   └── DonorResponse.java
    │   ├── entity/
    │   │   ├── BloodGroup.java
    │   │   ├── DonationRecord.java
    │   │   └── Donor.java
    │   ├── exception/
    │   │   ├── BusinessRuleException.java
    │   │   ├── ErrorResponse.java
    │   │   ├── GlobalExceptionHandler.java
    │   │   └── ResourceNotFoundException.java
    │   ├── repository/
    │   │   ├── BloodGroupRepository.java
    │   │   ├── DonationRecordRepository.java
    │   │   └── DonorRepository.java
    │   └── service/
    │       ├── BloodGroupService.java
    │       ├── DonationService.java
    │       └── DonorService.java
    └── resources/
        └── application.properties
pom.xml
mvnw
mvnw.cmd
```

## 14. Running the Project

Prerequisites: Java 25, MySQL, and the included Maven Wrapper. Ensure MySQL is running and the `bloodlink` database exists. Database credentials are supplied through `DB_USERNAME` and `DB_PASSWORD`; do not put real passwords in source control.

In PowerShell, from the project root, set the credentials for the current session and start the app:

```powershell
$env:DB_USERNAME = Read-Host 'MySQL username'
$securePassword = Read-Host 'MySQL password' -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $securePassword).Password
.\mvnw.cmd spring-boot:run
```

The password prompt does not echo input. These variables apply only to the current PowerShell session. Check `src/main/resources/application.properties` for the JDBC URL and Hibernate development setting. Once the application starts, open the Swagger UI URL above.

## 15. Testing

The REST API has been manually exercised through Swagger UI/Postman. The tested functionality includes donor registration and retrieval, donor search and eligibility filtering, donation recording and last-donation-date update, donation history, blood-group statistics, validation/error handling, and Swagger/OpenAPI availability.

## 16. Future Enhancements

The following are possible future enhancements and are not current features:

- Authentication and role-based access.
- Email or SMS notifications.
- Location/radius-based search.
- An administrative dashboard.
- Cloud deployment.

## 17. Conclusion

BloodLink currently provides a layered REST API for donor registration and search, service-level enforcement of the 90-day eligibility cooldown, transactional donation recording, donation history, blood-group statistics, request validation, structured error responses, and Swagger/OpenAPI documentation.
