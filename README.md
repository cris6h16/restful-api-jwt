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

#### 1.5.2 Flexibility in Clean Architecture

#### 1.5.3  Code organization.

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
            1. a src code tree for **Domain** ('inside' - everything that is independent of any technology ): `OrdersService`, `OrdersServiceImpl`, `Orders` (db gateway)
            2. another src code tree for **Infrastructure**: The _outside_ (e.g., controllers, repositories, frameworks, ect). 
            - **Infrastructure** has a compile-time dependency of **Domain** ( by modules or projects in your build tool (Maven, Gradle, etc).)
            - be careful: 'outside' layers can communicate with other 'outside' layers (e.g., controllers with repositories).   



**CONSIDERATIONS**:   

- **Package-Private**: `Public`: Only expose classes that are needed by external packages, otherwise keep them `package-private`.
- Before the choosing: consider the size of your team, their skill level, and the complexity of the solution in conjunction with your time and budgetary constraints.    


<hr>    

_**Note:**_ All content here about the design architecture used is based on the book _"Clean Architecture: A Craftsman's Guide to Software Structure and Design"_ by Robert C. Martin.

### 1.5 Sequences

![Sequences diagram](docs/imgs/sequences.jpg)
