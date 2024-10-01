package org.cris6h16.Config.SpringBoot.Properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*

email-service:
  host: http://localhost:4200
  token:
    parameter: token
    variable-in-link-template: "{token}"

  verification:
    link-template: ${email.host}/auth/verification-email?${email.token.parameter}=${email.token.variable-in-link-template}
    subject: "Email verification"
    html:
      name: "email-verification.html"
      href-variable: "link"

  reset-password:
    link-template: ${email.host}/auth/reset-password?${email.token.parameter}=${email.token.variable-in-link-template}
    subject: "Reset password"
    html:
      name: "reset-password.html"
      href-variable: "link"

  delete-account:
    link-template: ${email.host}/me/delete-account?${email.token.parameter}=${email.token.variable-in-link-template}
    subject: "Delete account"
    html:
      name: "delete-account.html"
      href-variable: "link"

  update-email:
    link-template: ${email.host}/me/update-email?${email.token.parameter}=${email.token.variable-in-link-template}
    subject: "Update email"
    html:
      name: "update-email.html"
      href-variable: "link"

 */
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
