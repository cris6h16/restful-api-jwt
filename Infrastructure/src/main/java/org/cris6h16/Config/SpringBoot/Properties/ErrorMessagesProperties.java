package org.cris6h16.Config.SpringBoot.Properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
error:
  messages:
    system:
      unexpected: "Unexpected error, please try again later"
    user:
      not-found: "User not found"
      email-not-verified: "Email not verified, please check your email"
      updating-password:
        password-not-match: "Your current password not matches"
      login:
        invalid-credentials: "Invalid credentials"
      unique:
        username: "Username already exists"
        email: "Email already exists"
      invalid:
        email: "Invalid email"
        id:
          null-val: "Id cannot be null"
        username:
          length: "Username length must be between 3 & 20 ( included )"
        password:
          length:
            tooShort: "Password must be at least 8 characters long"
        roles:
          empty: "A user must have at least one role"

 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "error.messages")
public class ErrorMessagesProperties {
    private System system;
    private User user;

    @Getter
    @Setter
    public static class System  {
        private String unexpected;
    }

    @Getter
    @Setter
    public static class User {
        private String notFound;
        private String emailNotVerified;
        private UpdatingPassword updatingPassword;
        private Login login;
        private Unique unique;
        private Invalid invalid;

        @Getter
        @Setter
        public static class UpdatingPassword {
            private String passwordNotMatch;
        }

        @Getter
        @Setter
        public static class Login {
            private String invalidCredentials;
        }

        @Getter
        @Setter
        public static class Unique {
            private String username;
            private String email;
        }

        @Getter
        @Setter
        public static class Invalid {
            private String email;
            private Id id;
            private Username username;
            private Password password;
            private Roles roles;

            @Getter
            @Setter
            public static class Id {
                private String nullVal;
            }

            @Getter
            @Setter
            public static class Username {
                private String length;
            }

            @Getter
            @Setter
            public static class Password {
                private Length length;

                @Getter
                @Setter
                public static class Length {
                    private String tooShort;
                }
            }

            @Getter
            @Setter
            public static class Roles {
                private String empty;
            }
        }
    }
}
