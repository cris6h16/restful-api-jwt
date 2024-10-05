package org.cris6h16.Config.SpringBoot.Properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


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
