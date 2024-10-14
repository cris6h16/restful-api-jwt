package org.cris6h16.Config.SpringBoot.Properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Component
@ConfigurationProperties(prefix = "controller")
@Getter
@Setter
public class ControllerProperties {
    private String base;

    private Authentication authentication;
    private User user;

    @Getter
    @Setter
    public static class Authentication {
        private String core;
        private String signup;
        private String login;
        private String verifyEmail;
        private String requestResetPassword;
        private String resetPassword;
        private String refreshAccessToken;
    }

    @Getter
    @Setter
    public static class User {
        private String core;
        private Pagination pagination;
        private Account account;

        @Getter
        @Setter
        public static class Pagination {
            String all;
        }

        @Getter
        @Setter
        public static class Account {
            private String core;
            private Request request;
            private Update update;

            @Getter
            @Setter
            public static class Request {
                private String core;
                private String delete;
                private String updateEmail;

            }

            @Getter
            @Setter
            public static class Update {
                private String core;
                private String username;
                private String password;
                private String email;

            }

        }

    }
}
