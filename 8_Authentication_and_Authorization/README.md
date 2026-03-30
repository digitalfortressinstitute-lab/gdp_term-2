# 🔐 Module 8 — Authentication & Authorization

> **GDP Term 2 | Digital Fortress Institute**
> Diploma in Full Stack Software Development | Year 1, Term 2

---

## 📌 Module Overview

This module builds on the secured APIs you built in **Module 7** and adds full **user identity management**. You will implement industry-standard authentication and authorization patterns using **JWT** and **OAuth2** in **Java Spring Boot**.

> **Authentication** = Who are you? (identity)
> **Authorization** = What are you allowed to do? (permissions)

---

## 📚 What You Will Learn

| Topic | Description |
|---|---|
| Auth Fundamentals | AuthN vs AuthZ, sessions vs tokens |
| JWT | Structure, signing, validation, refresh tokens |
| Spring Security | Filter chain, `SecurityFilterChain`, password encoding |
| JWT Implementation | Login/register endpoints, stateless JWT auth |
| Role-Based Access Control | `@PreAuthorize`, `ROLE_USER` vs `ROLE_ADMIN` |
| OAuth2 Basics | Authorization Code flow, resource server setup |
| Security Best Practices | Token storage, HTTPS, secret rotation |

---

## 🗂️ Files

| File | Description |
|---|---|
| 📖 [Course Notes](./Authentication_Authorization_Course_Notes.md) | Full theory + code examples |
| 💻 [01 — Auth Fundamentals](./examples/01_Auth_Fundamentals.java) | AuthN vs AuthZ, sessions vs tokens, JWT structure |
| 💻 [02 — Password Encoding](./examples/02_Password_Encoding.java) | BCrypt hashing with Spring Security |
| 💻 [03 — Spring Security Setup](./examples/03_Spring_Security_Setup.java) | `SecurityFilterChain`, CSRF, stateless config |
| 💻 [04 — JWT Implementation](./examples/04_JWT_Implementation.java) | JWT generation, validation, filter |
| 💻 [05 — Register & Login](./examples/05_Register_Login.java) | Auth endpoints, User entity, UserDetailsService |
| 💻 [06 — Role Based Access](./examples/06_Role_Based_Access.java) | `@PreAuthorize`, method security, admin/user roles |
| 💻 [07 — OAuth2 Basics](./examples/07_OAuth2_Basics.java) | OAuth2 flow, resource server, Google login |
| 💻 [08 — Full Auth API](./examples/08_Full_Auth_API.java) | Complete auth system: register, login, refresh, RBAC |

---

## 🧭 Learning Path

```
Module 7 (REST APIs)
        ↓
Auth Fundamentals → Password Encoding → Spring Security Setup
        ↓
JWT Implementation → Register/Login Endpoints → Role-Based Access
        ↓
OAuth2 Basics → Full Auth API (Capstone)
```

---

*Module 8 — Authentication & Authorization | GDP Term 2 | Digital Fortress Institute*
