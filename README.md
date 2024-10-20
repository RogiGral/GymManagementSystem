Gym Management System
=====================

Project Overview
----------------

The **Gym Management System** is a comprehensive web application designed to streamline the management of gym operations. Built with **Spring Boot** and **Angular**, it provides gym administrators with the tools they need to efficiently manage memberships, workout schedules, equipment, and more. With its user-friendly interface, the system simplifies the day-to-day operations of running a gym, offering features like user registration, membership plans, workout tracking, and QR code integration for easy access and attendance tracking.

Key Features
------------

-   **User Management**: Facilitates member registration, login, and profile management.
-   **Membership Plans**: Allows users to select, upgrade, or renew gym membership plans.
-   **Workout Scheduling**: Enables members to book workout sessions with trainers, set up personalized schedules, and track their progress.
-   **Equipment Management**: Tracks gym equipment availability and maintenance schedules.
-   **Attendance Tracking**: Monitors member attendance and generates usage reports.
-   **QR Code Integration**: Provides QR code generation and scanning for seamless membership validation and attendance tracking.

Tech Stack
----------

-   **Backend**:

    -   [Spring Boot 3.2.5](https://spring.io/projects/spring-boot)
    -   Hibernate
    -   PostgreSQL (primary database)
    -   AWS DynamoDB (optional, for enhanced scalability)
    -   Maven (build tool)
-   **Frontend**:

    -   [Angular 15](https://angular.io/)
    -   TypeScript
    -   HTML5, CSS3
-   **Other Tools**:

    -   Docker (for containerized deployment)
    -   WSL Ubuntu (development environment)
    -   Angularx-qrcode (for QR code generation)

Installation
------------

### Prerequisites

-   Java 17 or higher
-   Node.js and npm (for Angular frontend)
-   Docker (for containerization)
-   PostgreSQL database setup

### Backend Setup (Spring Boot)

1.  Clone the repository:\
    `git clone https://github.com/RogiGral/GymManagementSystem.git`
2.  Navigate to the backend folder:\
    `cd GymManagementSystem/backend`
3.  Configure the `application.yml` file with your database settings.
4.  Build the project:\
    `./mvnw clean install`
5.  Run the application:\
    `./mvnw spring-boot:run`

### Frontend Setup (Angular)

1.  Navigate to the frontend folder:\
    `cd GymManagementSystem/frontend`
2.  Install dependencies:\
    `npm install`
3.  Run the Angular app:\
    `ng serve`
4.  Open the browser and navigate to:\
    `http://localhost:4200`


Contribution
------------

Contributions are welcome! Follow these steps to contribute:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Commit your changes and push to your branch.
4.  Submit a pull request.

License
-------

This project is licensed under the MIT License. See the [LICENSE](https://github.com/RogiGral/GymManagementSystem/blob/main/LICENSE) file for details.

* * * * *

This polished version improves the flow and structure while maintaining the same core content and technical details.
