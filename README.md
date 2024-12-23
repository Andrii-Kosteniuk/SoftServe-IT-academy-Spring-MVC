# Practical Java Course

## Web Application ‘ToDo List’

1. Remove comments in all classes in Controller package and add needed code there

### Create Web Application with:

    1. 'User' page for displaying information about all registered users
    2. 'Update existing user' for updating a user
    3. 'Create New User' page for creating a new user
    4. 'All ToDo lists of <user>' page with showing information about all ToDo lists of some user and adding new ToDo list
    5. 'Update ToDo list' for updating an existing ToDo list
    6. 'Create New ToDo list' page for creating a new ToDo list of some user
    7. 'All Tasks of ToDo list' page where user can see all tasks from some ToDo list and add a new task
    8. 'Create New Task' for creating a new task for some ToDo list of concrete user
    9. 'Update Task' page some existing task

### 'Home' page

'Home' page should contain image with some text (see mockup).

### Menu

    1. Menu should be present on all pages
    2. When the user clicks on the 'ToDos Lists' logo or `Home` word on any page 'Home' page should be opened

### 'User' page

'User' page should contain information about all registered users.

    1. There should be a menu and a table with information about all registered users
    2. When a user clicks on ‘Edit’ link near some user 'Update Existing User' page with filled data 

about the selected user should be opened

3. When a user clicks on ‘Remove’ link the corresponding user should be deleted
4. When a user clicks on the 'Create New User' button 'Create New User' page with empty fields should be opened
5. When the user clicks on the name of some user All ToDo lists of <user> should be opened

### 'Update Existing User' page

‘Update existing User’ page should contain filled form with information about the selected user and 'Update' and 'Clear'
buttons

    1. When a user clicks on ‘Clear’ button all fields should be cleared
    2. When the user clicks on 'Update’ button' changed data should be saved and 'Home' page should be opened 

### ‘Create New User’ page

‘Create new User’ page should contain a form for creating a new user and 'Register' and 'Clear' buttons

    1. When a user clicks on ‘Clear’ button all fields should be cleared
    2. When the user clicks on 'Register’ button' all entered data should be saved and 'All ToDo Lists of <user>' page should be opened 

### 'All ToDo lists of <user>' page

'All ToDo lists of <user>' page should contain a table with information about all ToDo list some user and 'Create New
ToDo list' button

    1. When a user clicks on ‘Edit’ link near some list 'Update ToDo list' page with filled data about the selected list should be opened
    2. When a user clicks on ‘Remove’ link the corresponding list should be deleted
    3. When a user clicks on the 'Create New ToDo list' button 'Create New ToDo List' page with empty fields should be opened
    4. When the user clicks on the name of some list 'All Tasks of <ToDo Lists>' should be opened

### 'All Tasks From <User's ToDo List>'

'All Tasks From <User's ToDo List> should contain 'Create Tasks' button, 'Tasks' table, dropdown for adding
collaborators, list all collaborators, and 'Go to ToDo Lists' link

### 'Create New Task' page

'Create New Task' page should contain 'Name' fields, "Priority' dropdown for choosing task priority (e.g. High, Medium,
Low), 'Create' and 'Clear' button and 'Go to Task List' link

### 'Update Task' page

'Update Task' page should contain 'Id' (disabled), 'Name' fields, 'Priority' and 'Status' dropdowns, 'Update' and '
Clear' buttons, also there should be added 'Go to Task List' link

Create also pages

    'Create New ToDo List'
    'Update ToDo List' 

Requirements for these pages are the same as the previous descriptions

Implement all needed controllers and templates

*You can use a template project as a basis
Submit links to your GitHub repository and make a short video (2-5 minutes) where demonstrate the functionality as the
result of your work

## Mockup examples (using style files are optional)

### 1. 'Home' page

<img src="mockups/home.png" alt="alt text" width="400" />

### 2. 'Update Existing User' page

<img src="mockups/update_user.png" alt="alt text" width="400" />
<!-- ![](mockups/update_user.png) -->

### 3. 'Create New User' page

<img src="mockups/create_user.png" alt="alt text" width="300" />
<!-- ![](mockups/create_user.png) -->

### 4. 'All ToDo Lists of <User>' page

<img src="mockups/all_todos.png" alt="alt text" width="400" />
<!-- ![](mockups/all_todos.png) -->

### 5. 'All Tasks From <user's> ToDo List'

<img src="mockups/all_tasks.png" alt="alt text" width="400" />
<!-- ![](mockups/all_tasks.png) -->

### 6.'Create New Task' page

<img src="mockups/create_task.png" alt="alt text" width="300" />
<!-- ![](mockups/create_task.png) -->

### 7. 'Update Task' page

<img src="mockups/update_task.png" alt="alt text" width="400" />
<!-- ![](mockups/update_task.png) -->

### 8. 'Users' page

<img src="mockups/users.png" alt="alt text" width="400" />
<!-- ![](mockups/users.png) -->

## Set Up DB

When application starts your DB will be filled data from data.sql file from resources folder

There are three users with ADMIN and USER roles in DB.

| Login         | Password | Role  |
|---------------|:--------:|:-----:|
| mike@mail.com | Qwerty0# | ADMIN |
| nick@mail.com | Qwerty1! | USER  |
| nora@mail.com | Qwerty2@ | USER  |

User with Admin role has access to all data and resources in DB

# ToDo List Web Application

## Introduction

The **ToDo List Web Application** is a feature-rich platform designed to manage users, to-do lists, and tasks with a web
interface. It incorporates role-based user management, CRUD operations for users, lists, and tasks, and intuitive
navigation with a consistent menu. The application is built with Spring Boot, Hibernate, Thymeleaf templates, and a
MySQL database, and it includes pre-populated sample data.

---

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Setup and Installation](#setup-and-installation)
- [Database Configuration](#database-configuration)
- [Tests](#Tests)

---

## Features

1. **Role-Based User Management**:
    - Admin and user roles with login functionality.
    - Preloaded users for quick testing.

2. **User Management**:
    - View all users.
    - Create, update, and delete users.

3. **ToDo List Management**:
    - View all to-do lists for a user.
    - Create, update, and delete to-do lists.

4. **Task Management**:
    - View tasks within a to-do list.
    - Create, update, and delete tasks.
    - Assign priorities and statuses to tasks.

5. **Collaborators**:
    - Add and list collaborators for to-do lists.

6. **Dynamic Navigation Menu**:
    - Consistent navigation across all pages.

7. **Database Setup**:
    - Database initialized with `data.sql` for quick testing.

---

## Technologies Used

- **Backend**: Spring Boot, Spring Data JPA, Hibernate
- **Frontend**: Thymeleaf, HTML5, CSS3
- **Database**: PostgreSql for production, H2 for testing
- **Testing**: JUnit, MockMvc

---

## Setup and Installation

### Prerequisites

- **Java**: JDK 11 or later
- **Maven**: Build automation tool
- **PostgreSQL**: Database server
- **Git**: Version control system

### Steps to Run the Application

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Andrii-Kosteniuk/SoftServe-IT-academy-Spring-MVC.git

## **Database Configuration**

    - Update application.properties in the src/main/resources folder:

``` properties
server.port=8083

spring.application.name=todolist
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=root

spring.jpa.show-sql=true
spring.jpa.generate-ddl=true
spring.jpa.defer-datasource-initialization=true
spring.jpa.hibernate.ddl-auto=create-drop
spring.sql.init.mode=always
```

3. **Run the Application:**

    - Build and run:

```bash 
mvn spring-boot:run
```
## Tests

   - Executing a tests
```bash
mvn test