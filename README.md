# Introduction

*To be completed.*

---

# Objectives

*To be completed.*

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

Contraints
- Each Student, Tutor, Booking and payment must have a unique identifier
- A booking can only be created if both the student and tutor exist
- All data is tored in a relational database using JPA
- The system is developed using SPring Boot and follows a layered architecture (Controller, Service, Repository and Domain)

Assumptions
- Users provide accurate and complete informationduring registration and booking
- Tutors are available for the time slots selected by students
- The database is running and properly confiqured before the application starts
- The application is used in a secure environment where user credentials are kept confidential 
---

# Expected Outcomes

- Students can successfully create and manage tutoring session bookings
- Tutors can be registered and managed within the system
- Administrators can perform Create, Read, Update and Delete operations on all entities
- Booking information is stored and retrieved frpm database using JPA
- Relationships between Students, Tutors, Bookings and Payments are maintained correctly
- The system provides a reliable and user friendly backend fr managing tutoring services
