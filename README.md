## 1 Design

### 1.1 JWT Structure

#### 1.1.1 `RefreshToken`

Used to generate a new `AccessToken` when it expires, this one is exclusively used
by the client just to get a new `AccessToken`.

- Lifetime: 15 days

```
{
  "sub": "1",
  "exp": 12345678,
}
```

#### 1.1.2 `AccessToken`

Used to authenticate the user, this one is used by the client to access the API.

- Lifetime: 30 minutes

```
{
  "sub": "1",
  "exp": 123456,
  "roles": ["ROLE_USER"]
}
```

### 1.3 Token Storage

Tokens are commonly stored in **Local Storage** or **Session Storage** on the client side. This method simplifies
accessing the token when sending requests with the token in the `Authorization` header, as required by standards such as
**OAuth2**, which mandates that tokens be included in the `Authorization` header for secured endpoints.

#### Security Considerations

- **CSRF Protection:** Storing the token in Local/Session Storage helps prevent **CSRF** attacks since the token must be
  manually added to the `Authorization` header and is not automatically included in requests like cookies.
- **XSS Vulnerability:** However, this approach can be vulnerable to **XSS** attacks, as JavaScript running on the
  client can potentially access the token.

#### Best Practices for Secure Token Storage

During a workshop, the following insights were highlighted:

- **JWTs** stored in Local/Session Storage and accessible via JavaScript are susceptible to **XSS** attacks.
- **JWTs** securely stored in cookies offer enhanced protection against both **XSS** and **CSRF** attacks.

  **Using Cookies:**
    - **HttpOnly:** Prevents **XSS** attacks by making the cookie inaccessible to JavaScript.
    - **Secure:** Ensures the cookie is only sent over **HTTPS**, safeguarding it during transmission.
    - **SameSite:** Prevents **CSRF** attacks by restricting the contexts in which the cookie is sent (e.g., strict mode
      only sends cookies in first-party contexts).
    - **Path:** Limits the scope of the cookie, ensuring it is only sent to specified paths.
    - **Max-Age:** Defines the cookie’s lifetime, after which it expires and is deleted.

By storing **JWTs** in secure, HttpOnly cookies, you can significantly enhance the security of token storage on the
client side, protecting against common web vulnerabilities like **XSS** and **CSRF** attacks.

### 1.4 Cache

#### 1.4.1 Redis

**Redis** is an open-source, in-memory data structure store.

#### 1.4.2 Cache Strategies

as we know, we can use redis as a distributed cache, applying the most convenient cache strategy:

1. Cache-Aside:
    - **Reads**:
        1. **App** Check the cache

        - if found (Cache Hit), return the result to the client.
        - if not found (Cache Miss):

        3. **App** read from the database
        4. **App** store the result in the cache
        5. **App** return the result to the client.

    - **Writes**:
        1. **App** write to the database

    - **Pros**:
        - Ideal for read-heavy apps.
        - If cache is down, the app can still work.

    - **Cons**:
        - For new data (write) , always will be Cache Miss ( pre-heat the cache for solve )
        - Cache can be inconsistent with the database if the write strategy is not the appropriate one. ( when update
          data in db )
        - The way you store the data in the cache can be different from the way that data is stored in the database. (
          App has the responsibility to read/write the cache )

2. Read-Through:
    - **Reads**:
        1. **App** check the cache

        - if found (Cache Hit), **Cache** return the result to the **App**.
        - if not found (Cache Miss):

        2. **Cache** read from the database
        3. **Cache** itself store the result in the cache
        4. **Cache** return the result to the **App**

    - **Writes**:
        1. **App** write to the database

    - **Pros**:
        - Ideal for read-heavy apps.
        - Logic for fetching data from DB and update cache is handled separate from the application.

    - **Cons**:
        - For new data(write), always will be Cache Miss ( pre-heat the cache for solve )
        - Cache can be inconsistent with the database if the write strategy is not the appropriate one. ( when update
          data in db )
        - The way you store the data in the cache should be the same as the table in the database. ( Avoids complexity )

3. Write Around:
    - **Writes**:
        1. **App** write to the database
        2. **App** invalidate the cache ( e.g. using a flag )
    - **PROS**:
        - Just recommended for read-heavy application ( with Read-Through or Cache-Aside - alone shouldn't be used )
        - Inconsistent between cache and database is solved.
    - **CONS**:
        - If DB is down, write will fail.


4. Write-Through:
    - **Writes**:
        1. **App** write to the cache
        2. **App** synchronously write to the database

    - **PROS**:
        - Consistent: cache and database.
        - Cache Hit rate is higher.

    - **CONS**:
        - Alone used just increase the latency of the write operation ( use this with Read-Through or Cache-Aside )
        - For maintain the transactional property, we need support 2 phase commit.
        - If DB is down, write will fail.


5. Write-Behind (Write-Back):
    - **Writes**:
        1. **App** write to the cache
        2. **App** ASYNCHRONOUS write to the database ( sent to a queue )

    - **PROS**:
        - Ideal for write-heavy applications.
        - Write latency is reduced. ( write in DB is async )
        - Cache Hit rate is higher.
        - better if is used with **Read-Through**.
        - If DB is down, write will not fail. ( even for a long time - regarding the cache live time )

    - **CONS**:
        - Potential Inconsistency: If data is lost from the cache (e.g., cache entry expires) before the asynchronous
          write is completed, there could be data inconsistencies. ( solve with a setting the TAT (Turnaround Time) of
          cache little higher
          like 2 days )

<hr>

**IN THIS PROJECT:**   

I am going to use **Cache-Aside** strategy, using Redis.

### 1.5 Design Architecture

This project is going to be developed using the **Clean Architecture**.   
as we know, this architecture is the integration of several architectures:

1. [**Hexagonal Architecture (also known as Ports and Adapters)
   **](https://en.wikipedia.org/wiki/Hexagonal_architecture_(software))
2. [**Data, context and interaction**](https://en.wikipedia.org/wiki/Data,_context_and_interaction)
3. [**Entity-control-boundary**](https://en.wikipedia.org/wiki/Entity-control-boundary)
4. ...

All of these architectures have the same goals:

- **Independent of frameworks.**
- **Testable.**
- **Independent of UI.**
- **Independent of Database.**
- **Independent of any external agency.**

#### 1.5.1 Strictness in Clean Architecture

![Clean Architecture](https://blog.cleancoder.com/uncle-bob/images/2012-08-13-the-clean-architecture/CleanArchitecture.jpg)

- **Workflows:**
    - An element in an _Inner Circle_ must not depend on any element in an _Outer Circle_.
    - If it needs to use an element in an _Outer Circle_, it must do so through an **Interface**.
    - Each _Circle_ should only interact with the adjacent _Circle_; no direct jumps between layers are allowed.

- **Layers:**
    - **Entities:**
        - Represent critical business rules that would exist even if no software were present.
    - **Use Cases:**
        - Define and constrain how the automated system operates (application-specific business rules).
    - **Interface Adapters:**
        - Convert data between the format used by the use cases and entities, and the format required by external
          systems like databases or web services.
    - **Frameworks and Drivers:**
        - Handle the details of external interfaces, tools, and devices.
    - **Other:**
        - can have more layers.

#### 1.5.2 Flexibility in Clean Architecture

#### 1.5.2.3  Code organization.

1. **Package by Layer:**
    - Horizontal layering
    - should depend only on the next adjacent lower layer
    - Java: layer == package
    - the best for start a project
    - Quick way to get something up and running without a huge amount of complexity
    - The bigger the project, the less ideal this approach becomes
    - As it grows it should evolve into a more modularized structure.
    - Doesn’t scream anything about the business domain.
        - Hard: Understand what the business does just by looking at the code.
        - Hard: Maintain or extend business-specific features.
        - Hard: Communicate the domain effectively to new developers or stakeholders.
    - Example of layers: `Web`, `Service`, `Data`


2. **Package by Feature:**
    - Vertical layering
    - Gather all classes based on related features, domain concepts, or aggregates roots (DDD).
    - Package name reflects the concept or feature.
    - scream about the business domain:
        - the organization dont say: `Web`, `Service`, `Data`
        - says: `Orders`, `Products`, etc.
    - This vertical layering is preferred instead of horizontal layering.
    - Example:
         ```text
         com.example.app.orders >> ( OrdersController, OrdersService, OrdersServiceImpl, OrdersRepository, InMemoryOrdersRepository )
      ```   


3. **Ports and Adapters:**
    - business/domain-focused code is independent of specific technologies such as databases, frameworks, etc.
    - Composed of:
        - `Domain`: inside
        - `Infrastructure`: outside
            - UIs, Databases, Third-party integrations, etc.
    - **Important**: **Infrastructure** depends on **Domain**, but **Domain** doesn't depend on **Infrastructure**.
    - **Inside** should be stated in terms of _Ubiquitous domain language (DDD)_.
        - Example: `OrderRepository` >> renamed to >> `Orders`


4. **Package by Component**:
    - It’s a hybrid approach to everything we’ve seen so far.
    - **Goal**: all the related responsibilities into a single coarse-grained component into a single Java package.
    - Keeps the UI separate from these coarse-grained components ( as in Ports and Adapters ).
    - Bundles: Business logic & Persistence code together ( component ).
    - think as: a software system is made up of:
        1. **Containers**: Web Apps, Mobile Apps, Databases, etc.
        2. **Components**:
            - A group of related functions behind a clean interface ( A clean interface in the exposed input/output of
              the component ).
            - inside the separation of concerns is applied.
            - Example: `OrdersComponent.jar`, `UserManagementComponent.jar`, etc.
        3. **Classes**: The related classes that make up the component.

        - How **Components** are packaged doesn't matter (Examples):
            1. **Separate JAR Files**: Each component is packaged into its own JAR file. These JARs are managed and
               included in
               the classpath or module path when running the application.
                - `OrdersComponent.jar`, `UserManagementComponent.jar`.

            2. **Combined JAR File**: All components are bundled together in a single JAR file. This approach simplifies
               deployment but combines all functionality into one file.

            - `application.jar` >> Contains (e.g. `OrdersComponent.jar`, `UserManagementComponent.jar`) combined into
              one JAR.

            3. etc... it can vary depending on your concept of "Component".
    - Pro: if you want to write code that needs to do something with orders, you know exactly where to look;
      in `OrdersComponent`.
    - Can start as a monolith and then easily evolve into a microservices architecture ( our components are
      well-defined ).
    - Example:
      ```
        com.mycompany.myapp
        │
        ├── web
        │   └── OrdersController.java
        │
        └── orders
            ├── OrdersComponent.java
            ├── OrdersComponentImpl.java ( package private )
            ├── OrdersRepository.java    ( package private )
            └── InMemoryOrdersRepository.java ( package private )
      ```    


5. **Other Decoupling Modes**:
    1. Java 9 module system ( ensures a strict decoupling )
    2. Source Code level decoupling (different source code trees for each):
        - Examples with **ports and adapters**:
            1. a src code tree for **Domain** ('inside' - everything that is independent of any
               technology ): `OrdersService`, `OrdersServiceImpl`, `Orders` (db gateway)
            2. another src code tree for **Infrastructure**: The _outside_ (e.g., controllers, repositories, frameworks,
               ect).

            - **Infrastructure** has a compile-time dependency of **Domain** ( by modules or projects in your build
              tool (Maven, Gradle, etc).)
            - be careful: 'outside' layers can communicate with other 'outside' layers (e.g., controllers with
              repositories).

**CONSIDERATIONS**:

- **Package-Private**: `Public`: Only expose classes that are needed by external packages, otherwise keep
  them `package-private`.
- Before the choosing: consider the size of your team, their skill level, and the complexity of the solution in
  conjunction with your time and budgetary constraints.

<hr>   


**IN THIS PROJECT:**

I'm going to follow the approach of `Ports and Adapters`, each boundary as an independent project (Maven).


<hr>      

_**Note:**_ All content here about the design architecture used is based on the book _"Clean Architecture: A Craftsman's
Guide to Software Structure and Design"_ by Robert C. Martin.

### 1.5 Use Cases   

![Use Cases](docs/imgs/UseCase_Diagram.png)

### 1.6 Sequences

![Sequences diagram](docs/imgs/sequences.png)


## 2. Development

### 2.1 Used Technologies

- _Java_
- _Maven_
- _Spring Boot_
  - _Spring Security_
  - _Spring Data JPA_
  - _Spring Data Redis_
  - _Spring Thymeleaf_
  - _Spring MVC_
  - _Spring Mail_
- _Testing_
  - _Spring Boot Test_
  - _JUnit 5_
  - _AssertJ_
  - _Mockito_
- _Docker Compose_
- _PostgreSQL_
- _OpenAPI_
- _Lombok_
- _Docker_
- _Redis_
- _JJWT_

### 2.2 Refactoring   

I know that there are some things that can be improved, like break down the
`.yaml` files into more-specific files, like `paths.yaml`, `jwt.yaml`, `responses.yaml`, etc.
this is one that I just remembered now, but honestly I can stay refactoring this project 
today, tomorrow, this week, this month and so on; and I will always find something to improve.

### 2.3 Questions you may have

#### 2.3.1 API Description Language

1. If I'm following the code-first approach, why did
I not use the `springdoc-openapi` library to generate 
the OpenAPI Description / client?   

> I have some reasons, the first is that I dont want write or edit the documentation
from the code, I prefer to write the documentation in a separate file, and the second
reason is that If I want to ignore a parameter like those that are injected by the 
Dependency Injection, I need go to the code and add the annotation to hide it 
as you can see Im modifying the code for the doc concern ( this only one example of those ), and another reason is that
I feel when I write the OpenAPI Description I have all the control of the documentation.

#### 2.3.2 Testing

1. Why did I not test the database with an embedded database like H2?

> I could do that, but I prefer to test the database with the real database, 
because I wanna test the real behavior ( of course in a different
environment, like a different schema, etc. ) even if those tests are slower.

2. What's about the coverage?

> I achieved 100% coverage in the `domain`, `application`, `infrastructure`.

_remember include only the project's packages in the coverage._  

_Infrastructure_   
![Infrastructure_scope.png](docs/imgs/testing/coverage/Infrastructure/scope.png)     

_Application_   
![Application_scope.png](docs/imgs/testing/coverage/Application/scope.png)        

_Domain_    
![Domain_scope.png](docs/imgs/testing/coverage/Domain/scope.png)       


- 100%    

_Infrastructure_    
![Infrastructure_result.png](docs/imgs/testing/coverage/Infrastructure/result.png)     

_Application_    
![Application_result.png](docs/imgs/testing/coverage/Application/result.png)   

_Domain_    
![Domain_result.png](docs/imgs/testing/coverage/Domain/result.png)   
