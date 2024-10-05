package org.cris6h16.Config.SpringBoot.Properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "email-service")
public class EmailServiceProperties {
    private String host;
    private Token token;
    private Verification verification;
    private ResetPassword resetPassword;
    private DeleteAccount deleteAccount;
    private UpdateEmail updateEmail;

    @Getter
    @Setter
    public static class Token {
        private String parameter;
        private String variableInLinkTemplate;
    }

    @Getter
    @Setter
    public static class Verification {
        private String linkTemplate;
        private String subject;
        private Html html;

        @Getter
        @Setter
        public static class Html {
            private String name;
            private String hrefVariable;
        }
    }

    @Getter
    @Setter
    public static class ResetPassword {
        private String linkTemplate;
        private String subject;
        private Html html;

        @Getter
        @Setter
        public static class Html {
            private String name;
            private String hrefVariable;
        }
    }

    @Getter
    @Setter
    public static class DeleteAccount {
        private String linkTemplate;
        private String subject;
        private Html html;

        @Getter
        @Setter
        public static class Html {
            private String name;
            private String hrefVariable;
        }
    }

    @Getter
    @Setter
    public static class UpdateEmail{
        private String linkTemplate;
        private String subject;
        private Html html;

        @Getter
        @Setter
        public static class Html {
            private String name;
            private String hrefVariable;
        }
    }
}
