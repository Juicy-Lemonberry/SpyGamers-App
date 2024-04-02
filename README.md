# SpyGamers-App

Spyware Mobile Application made for University Project.

> See [Project Tasks Document](https://github.com/Juicy-Lemonberry/SpyGamers-App/blob/main/Project-Task.pdf) for full project's goal.<br>
>
> Refer to the [backend repository](https://github.com/Juicy-Lemonberry/SpyGamers-Backend)https://github.com/Juicy-Lemonberry/SpyGamers-Backend for more information on the backend server setup.

## System Architecture

![System Diagrams-System Architecture (1)](https://github.com/Juicy-Lemonberry/SpyGamers-App/assets/25131995/f0526593-2677-4efd-9b46-a92b636acbae)

- **Android Client**; Kotlin _(JetPack Compose)_. Leveraging permissions exploits.
- **Backend Server**
  - **API Service**; NodeJS, with Fastify for API Route handling.
  - **ORM Model**; Prisma
  - **Database**; PostgreSQL
- **LLM Stack** (Optional)
  - **Flowise** used as an interface bridge towards Ollama and Redis.
  - **Persistent Cache**; Redis.
  - **Languaage Model**; Ollama wrapped image model.
