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

the fusion of these architectures for make the **Clean Architecture** finds for:

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
        - Convert data between the format used by the use cases and entities, and the format required by external systems like databases or web services.
    - **Frameworks and Drivers:**
        - Handle the details of external interfaces, tools, and devices.

#### 1.5.2 Flexibility in Clean Architecture

The **Clean Architecture** offers flexibility in how code is organized and packaged. The book recommends starting with a simple and straightforward design and evolving it as the system grows. Over time, components become more specific, depending on factors such as:

- Application size
- Complexity
- Number of developers
- Developer expertise

The organization of code is flexible, but the book emphasizes several key practices:

1. **Internal Package Organization:**
    - Only expose `public` classes that are needed by external packages.
    - Keep all other classes `package-private` (the default modifier in Java).
    - Organize packages by layer, feature, or component (considering principles like **Cohesion** and **Coupling**).

2. **Strict Separation:**
    - Use built-in language mechanisms for enforcing separation (e.g., Java 9 module system).
    - Ensure compile-time dependency management using tools like Maven or Gradle.
    - Example: Following Ports and Adapters architecture:
        - **Domain:** Represents the 'inside' core logic.
        - **Infrastructure:** Represents the 'outside' (e.g., controllers, repositories).
        - Be mindful: 'outside' layers can communicate with other 'outside' layers (e.g., controllers with repositories), but always ensure they pass through the 'inside'.

The book says that is very much an idealistic use the **Strict Separation**.

_**Note:**_ All content is based on the book _"Clean Architecture: A Craftsman's Guide to Software Structure and Design"_ by Robert C. Martin.

### 1.5 Sequences

![Sequences diagram](docs/imgs/sequences.jpg)
