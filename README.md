# Introduction

Tutoring Booking Service is a digital platform designed to connect university students with qualified tutors for academic support.
Many students struggle to find reliable tutors and book sessions without long WhatsApp chains and missed messages. This service 
solves that by allowing students to connect with available tutors, view subjects, check time slots and book tutoring sessions online in minutes.

The main goal is to make academic support more accessible, organized and stress-free for students while giving tutors a simple way to manage their bookings, students and payments all in one secure place.

---

# Objectives

- Simplify Booking: Allow students to search, book, pay for tutoring sessions effectively.
- Connect Students and Tutors: Match students with qualified tutors for their desired modules.
- Manage Schedules: Allow both students and tutors to have access to a clear calendar to avoid clashes and missed sessions.
- Increase Access: To make tutoring available to students anytime, anywhere even after school hours.
- Improves Results: To help students get academic support faster so they can improve their marks.
- Reduce No-Shows: Send automatic reminders to students and tutors before a session
- Build Trust: Allow students to rate and review tutors.
---

# TUTORING SERVICES UML
![TUTORING SERVICES UML](https://github.com/sabelo-ceza-26/CapstoneProject/blob/main/TUTORING%20SERVICE%20UML.svg)

---

# Scope

## In Scope

- User authentication for Students, Tutors, and Admins.
- Management of student, tutor, and subject information.
- Tutor-to-subject assignment.
- Booking, updating, and cancelling tutoring sessions.
- Payment recording and management.
- Administrative management of users, subjects, bookings, and payments.

## Out of Scope

- Online video tutoring.
- Messaging between students and tutors.
- Tutor ratings and reviews.
- Email or SMS notifications.
- Mobile application support.

---

# Constraints and Assumptions

Constraints
- Each Student, Tutor, Booking and payment must have a unique identifier
- A booking can only be created if both the student and tutor exist
- All data is stored in a relational database using JPA
- The system is developed using Spring Boot and follows a layered architecture (Controller, Service, Repository and Domain)

Assumptions
- Users provide accurate and complete information during registration and booking
- Tutors are available for the time slots selected by students
- The database is running and properly configured before the application starts
- The application is used in a secure environment where user credentials are kept confidential 
---

# Expected Outcomes

- Students can successfully create and manage tutoring session bookings
- Tutors can be registered and managed within the system
- Administrators can perform Create, Read, Update and Delete operations on all entities
- Booking information is stored and retrieved from database using JPA
- Relationships between Students, Tutors, Bookings and Payments are maintained correctly
- The system provides a reliable and user-friendly backend for managing tutoring services

# Security Features

The Tutoring Booking Service uses authentication and authorisation to protect the system and its users. When a user logs in, the system checks their email address and password to confirm their identity. After successful authentication, the system checks their assigned role to determine which features and information they are allowed to access.

- **Authentication:** The system uses HTTP Basic Authentication to verify users when they log in using their email address and password.
- **Authorisation:** The system has three user roles, each with different permissions:
  - **Student:** Can register, log in, view available subjects and tutors, create and manage their tutoring bookings, and access their payments.
  - **Tutor:** Can log in, manage their information,set their availability, manage the subjects they teach and access their bookings.
  - **Admin:** Has administrative access to manage students, tutors, subjects, bookings and payments.
- **Password Protection:** When users register, their passwords are protected using BCrypt before being stored in the database. This prevents passwords from being stored as plain text.
- **Email Verification:** Students and tutors must verify their email addresses before they can log in.
- **Login Protection:** The system limits repeated failed login attempts from the same IP address and user account to help prevent unauthorised access.
- **Registration Protection:** The system limits registration requests to reduce spam and prevent repeated account creation.
- **Access Error Handling:** When a user tries to access an endpoint without logging in or without the required role, the system returns an appropriate JSON error response.
- **Stateless Security:** The system uses stateless authentication, meaning it does not maintain server-side login sessions. CSRF protection is disabled because the backend is designed as a stateless API.

Together, these features help protect user information, secure booking and payment operations, and ensure that students, tutors and administrators can only perform actions permitted by their roles.
