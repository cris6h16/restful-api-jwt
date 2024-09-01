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

[//]: # (todo: - why i decided cache the users page with: private, max-age=600)

### 1.5 Design Architecture

### 1.5 Sequences

![Sequences diagram](docs/imgs/sequences.jpg)
