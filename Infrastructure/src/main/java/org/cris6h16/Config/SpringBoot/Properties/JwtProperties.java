package org.cris6h16.Config.SpringBoot.Properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey;
    private Token token;

    @Getter
    @Setter
    public static class Token {
        private Refresh refresh;
        private Access access;

        @Getter
        @Setter
        public static class Refresh {
            private Expiration expiration;
            private Cookie cookie;

            @Getter
            @Setter
            public static class Expiration {
                private long secs;
            }

            @Getter
            @Setter
            public static class Cookie {
                private String name;
                private String path;
            }
        }

        @Getter
        @Setter
        public static class Access {
            private Expiration expiration;
            private Cookie cookie;
            private Request request;

            @Getter
            @Setter
            public static class Expiration {
                private long secs;
            }

            @Getter
            @Setter
            public static class Cookie {
                private String name;
                private String path;
            }

            @Getter
            @Setter
            public static class Request {
                private Email email;

                @Getter
                @Setter
                public static class Email {
                    private Verification verification;
                    private DeleteAccount deleteAccount;
                    private UpdateEmail updateEmail;
                    private Reset reset;

                    @Getter
                    @Setter
                    public static class Verification {
                        private long secs;
                    }

                    @Getter
                    @Setter
                    public static class DeleteAccount {
                        private long secs;
                    }

                    @Getter
                    @Setter
                    public static class UpdateEmail {
                        private long secs;
                    }

                    @Getter
                    @Setter
                    public static class Reset {
                        private Password password;

                        @Getter
                        @Setter
                        public static class Password {
                            private long secs;
                        }
                    }
                }
            }
        }
    }
}
