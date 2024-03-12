# SpyGamers-App

Spyware Mobile Application made for University Project.

> See [Project Tasks Document](https://github.com/Juicy-Lemonberry/SpyGamers-App/blob/main/Project-Task.pdf) for full project's goal.<br>
>
> Refer to the [backend repository](https://github.com/Juicy-Lemonberry/SpyGamers-Backend)https://github.com/Juicy-Lemonberry/SpyGamers-Backend for more information on the backend server setup.

## System Architecture

![System Diagrams-System Architecture (1)](https://github.com/Juicy-Lemonberry/SpyGamers-App/assets/25131995/61e11897-0b98-4d4b-94a7-41e4aaa9aa59)

- **Android Client**; Kotlin _(JetPack Compose)_. Leveraging permissions and userspace exploits.
- **Backend Server**
  - **API Service**; NodeJS, with Fastify for API Route handling.
  - **ORM Model**; Prisma
  - **Database**; PostgreSQL
