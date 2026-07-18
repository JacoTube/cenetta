<div align="center">
  <h1>🍝 Cenetta</h1>
  <p>
    <strong>Piattaforma Full-Stack collaborativa per l'organizzazione di eventi conviviali</strong>
  </p>
  <p>
    <img src="https://img.shields.io/badge/Frontend-Angular_16-dd0031?style=flat-square&logo=angular" alt="Angular" />
    <img src="https://img.shields.io/badge/Backend-Spring_Boot_3.5-6db33f?style=flat-square&logo=spring" alt="Spring Boot" />
    <img src="https://img.shields.io/badge/Database-H2_in_memory-004b87?style=flat-square&logo=h2database&logoColor=white" alt="H2" />
    <img src="https://img.shields.io/badge/Security-JWT_Auth-black?style=flat-square&logo=jsonwebtokens" alt="JWT" />
    <img src="https://img.shields.io/badge/Concurrency-Optimistic_Locking-e8734a?style=flat-square" alt="Optimistic Locking" />
  </p>
</div>

<br />

## 📖 Panoramica del Progetto

**Cenetta** è un'applicazione web pensata per semplificare il problema di organizzare una cena tra amici, facendo in modo di dividere la mansioni tra gli utenti

Un **organizzatore** crea l'evento con una data e una scadenza, ottiene un codice di invito e lo condivide. Gli **invitati** entrano con codice e password, prendono in carico le task della checklist condivisa e propongono ciò che manca. Ogni operazione è immediatamente visibile a tutti.

Il cuore tecnico del sistema è la **gestione della concorrenza**: se due partecipanti tentano di accollarsi la stessa task nello stesso istante, solo uno riesce — l'altro riceve un feedback chiaro e la vista aggiornata.

Progetto realizzato per l'esame di *Piattaforme Software per Applicazioni su Web* — Università della Calabria.

## ✨ Funzionalità Principali

### 🔒 Sicurezza & Autenticazione (Stateless JWT)
- **Token-Based Auth**: nessuna sessione lato server. L'identità viaggia in un JWT firmato HMAC, verificato a ogni richiesta da un filtro dedicato.
- **Password mai in chiaro**: hash **BCrypt** con salt casuale, sia per gli account che per le password d'evento.
- **Identità non falsificabile**: l'utente non arriva mai dal body della richiesta, ma da `@AuthenticationPrincipal` — cioè dal token verificato.
- **Doppio livello d'accesso**: per entrare in un evento servono il codice di invito **e** la password scelta dall'organizzatore.

### ⚡ Concorrenza & Integrità (il cuore del progetto)
- **Locking Ottimistico**: campo `@Version` sulle task. Hibernate aggiunge `AND version = ?` a ogni UPDATE: se un'altra transazione è arrivata prima, zero righe vengono aggiornate e il conflitto viene rilevato.
- **Fallimento sicuro**: la seconda transazione effettua rollback e riceve un **409 Conflict**, mai una sovrascrittura silenziosa.
- **Feedback visivo**: il client intercetta il 409, avvisa l'utente e **ricarica la checklist**, mostrando immediatamente la situazione reale.
- **Congelamento passivo**: superata la deadline, ogni operazione di scrittura viene respinta con **423 Locked**. Nessuno scheduler: il controllo avviene al momento della richiesta.

### 🖥️ Frontend (Angular 16)
- **Single Page Application** con architettura a `NgModule`, routing dichiarativo e rotte protette da `CanActivateFn`.
- **HttpInterceptor**: allega automaticamente l'header `Authorization` a ogni chiamata — il gemello lato client del filtro JWT del backend.
- **Checklist reattiva**: le task si riorganizzano automaticamente per stato, con barra di avanzamento e pulsanti contestuali al ruolo dell'utente.
- **Osservabili RxJS**: composizione di chiamate asincrone con `tap` e `switchMap` (registrazione e login concatenati in un solo flusso).

### ⚙️ Backend (Spring Boot 3.5)
- **RESTful API**: architettura a strati — Controller (traduzione HTTP), Service (regole di business), Repository (persistenza).
- **DTO ovunque**: le entità non escono mai dai service. I DTO omettono gli hash, spezzano i cicli di serializzazione e aggiungono campi calcolati.
- **Gestione errori centralizzata**: un `@RestControllerAdvice` traduce le eccezioni di dominio in codici HTTP semantici (403, 404, 409, 423).
- **JPA & Hibernate**: relazioni `@ManyToOne`, `@ManyToMany` con join table, `@OneToMany` con cascade e orphan removal.

## 🛠️ Stack Tecnologico

| Layer | Tecnologie |
| :--- | :--- |
| **Frontend** | Angular 16, TypeScript, RxJS, HttpClient, CSS3 |
| **Backend** | Java 17, Spring Boot 3.5, Spring Security, Spring Web, Spring Data JPA |
| **Database** | H2 (in memory) — sostituibile senza modifiche al codice |
| **Sicurezza** | JWT (HMAC), BCrypt, filtri stateless |
| **Testing** | JUnit 5, Spring Boot Test — verifica automatica della concorrenza |
| **Build** | Maven Wrapper, Node.js / npm, Angular CLI |

## 🚀 Guida all'Avvio (Sviluppo Locale)

### Prerequisiti
- [Java JDK 17+](https://adoptium.net/)
- [Node.js](https://nodejs.org/) (v18 o superiore)

> [!NOTE]
> Non serve installare né configurare alcun database: H2 è in memoria e lo schema viene generato automaticamente da Hibernate all'avvio.

### 1. Avvio del Backend (Spring Boot)
```bash
cd backend
./mvnw spring-boot:run        # Windows: .\mvnw.cmd spring-boot:run
```
*(Il server si avvierà sulla porta `8080`. Console H2: `http://localhost:8080/h2-console` — JDBC URL `jdbc:h2:mem:cenettadb`, utente `sa`, password vuota)*

### 2. Avvio del Frontend (Angular)
```bash
cd frontend
npm install
ng serve
```
*(Il client si avvierà sulla porta `4200`)*

### 3. Esecuzione dei Test
```bash
cd backend
./mvnw test
```

> [!TIP]
> Il database viene ripopolato a ogni avvio da un `SeedService`, quindi si riparte sempre da uno stato pulito e prevedibile.

## 🔑 Credenziali Dimostrative

Tutti gli utenti condividono la password `password123`.

| Username | Nome visualizzato |
| :--- | :--- |
| `mario` | Mario Rossi |
| `luigi` | Luigi Verdi |
| `sinutaro` | Sinutaro Bianchi |

| Codice invito | Password | Evento | Stato |
| :--- | :--- | :--- | :--- |
| `CARBO123` | `risiko` | Carbonara e Risiko | 🟢 aperto |
| `PIZZA456` | `pizza` | Pizzata di fine sessione | 🟢 aperto |
| `PASTA789` | `pasta` | Pasta alla Norma | 🔒 scaduto |

L'ultimo evento ha la deadline nel passato: serve a dimostrare il congelamento passivo senza dover attendere una scadenza reale.

## 🔄 Ciclo di Vita di una Task

```text
                          proposta da un invitato
    (nuova) ──────────────────────────────────────► PENDING_APPROVAL
       │                                                    │
       │ creata dall'organizzatore           approva  ┌─────┴─────┐  rifiuta
       │                                              ▼           ▼
       └────────────────────────────────────────►  FREE       REJECTED
                                                 ▲    │ claim
                                        release  │    ▼
                                                 └ ASSIGNED ──complete──► COMPLETED
```

Ogni transizione è protetta da tre controlli in sequenza: **chi sei** (permesso), **puoi agire ora** (deadline), **lo stato lo consente** (transizione valida).

## 🌐 API REST

| Metodo | Endpoint | Descrizione |
| :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Registrazione utente |
| `POST` | `/api/auth/login` | Login, restituisce il JWT |
| `GET` | `/api/events` | Eventi organizzati o a cui si partecipa |
| `POST` | `/api/events` | Crea evento e genera il codice di invito |
| `POST` | `/api/events/join` | Accesso con codice + password d'evento |
| `GET` | `/api/events/{id}` | Dettaglio con partecipanti e checklist |
| `POST` | `/api/events/{id}/tasks` | Nuova task (proposta, se invitato) |
| `POST` | `/api/tasks/{id}/claim` | Prende in carico una task libera |
| `POST` | `/api/tasks/{id}/release` | Rilascia una task assegnata |
| `POST` | `/api/tasks/{id}/complete` | Marca come completata |
| `POST` | `/api/tasks/{id}/approve` | Approva una proposta *(organizzatore)* |
| `POST` | `/api/tasks/{id}/reject` | Rifiuta una proposta *(organizzatore)* |

**Codici di stato**: `401` non autenticato · `403` permesso negato · `404` risorsa inesistente · `409` conflitto di concorrenza · `423` evento congelato.

## 📁 Struttura del Progetto

```text
cenetta/
├── backend/                     # Codice sorgente Java Spring Boot
│   └── src/main/java/it/unical/cenetta/
│       ├── config/              # SecurityConfig, SeedService
│       ├── controller/          # Auth, Event, Task — traduzione HTTP
│       ├── dto/                 # Record di richiesta e risposta
│       ├── exception/           # Eccezioni tipizzate + GlobalExceptionHandler
│       ├── model/               # User, Event, Task, TaskStatus  ← @Version qui
│       ├── repository/          # Interfacce Spring Data JPA
│       ├── security/            # JwtService, JwtAuthenticationFilter
│       └── service/             # Regole di business, @Transactional
├── backend/src/test/java/       # OptimisticLockingTest
├── frontend/                    # Codice sorgente Angular
│   └── src/app/
│       ├── core/                # Servizi, Interceptor, Guard
│       ├── models/              # Interfacce speculari ai DTO
│       └── components/          # login, register, dashboard, event-detail
└── README.md                    # Questa documentazione
```

## 🔐 Architettura di Rete e Flusso Dati

1. Il client (Angular) invia richieste HTTP alle API esposte (`http://localhost:8080/api/...`).
2. Un `HttpInterceptor` intercetta ogni chiamata e inietta il token JWT nell'header `Authorization`.
3. Il `JwtAuthenticationFilter` di Spring Security verifica la firma, carica l'utente e popola il `SecurityContext`.
4. Il Controller traduce la richiesta e delega al Service, che applica le regole di business dentro una transazione.
5. Il Service restituisce un DTO — mai un'entità — che Jackson serializza in JSON.
6. In caso di errore, il `@RestControllerAdvice` traduce l'eccezione nel codice HTTP corrispondente, che il client interpreta per mostrare il feedback adeguato.

## 🧪 Come Dimostrare il Locking Ottimistico

### Dimostrazione visiva
1. Apri una finestra normale e una **in incognito**.
2. Accedi come `luigi` nella prima, `sinutaro` nella seconda.
3. Entrambi aprono *Carbonara e Risiko* e individuano la stessa task libera.
4. Cliccate **"Me ne occupo io"** quasi contemporaneamente.

Uno riceve la conferma. L'altro riceve il messaggio di conflitto **e vede la checklist aggiornarsi**, con la task ormai assegnata al primo.

### Dimostrazione automatica
```bash
./mvnw test
```
`OptimisticLockingTest` lancia due thread sincronizzati da un `CountDownLatch` che tentano il claim della stessa task. Le asserzioni verificano che **esattamente uno** faccia commit.

### Controprova
Commentando `@Version` in `Task.java` il test fallisce con `expected: <1> but was: <2>`: entrambe le transazioni riescono e una sovrascrive l'altra. Quel `2` è il **lost update**, la misura esatta di ciò che l'annotazione previene.

## 🎓 Mappa Tecnologie ↔ Programma del Corso

| Tecnologia / Concetto | Lezione |
| :--- | :---: |
| HTTP, metodi, codici di stato, header | 2 |
| Transazioni, proprietà ACID, isolamento | 3, 8 |
| Filtri servlet (`OncePerRequestFilter`) | 4, 5 |
| JPA, ORM, relazioni, lazy loading | 7, 8 |
| Spring: IoC, Dependency Injection, AOP | 9 |
| Spring Data JPA, `@Transactional`, `@Version` | 10 |
| Spring Security, `UserDetailsService`, JWT | 11 |
| Web service REST, `@RestController` | 12, 13 |
| TypeScript e gestione errori lato client | 14 |
| Angular: componenti, moduli, DI, routing | 15 |
| Angular: `HttpClient`, Observable RxJS | 16 |

Non sono state impiegate librerie esterne per l'interfaccia, ORM alternativi né framework di autenticazione di terze parti.

---

<div align="center">

*You're messin' up the water -*
*You rolling in the wine -*
*You're poisoning your body -*
*You're poisoning your mind*

</div>
