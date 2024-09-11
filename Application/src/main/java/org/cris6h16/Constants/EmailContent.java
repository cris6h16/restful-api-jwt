package org.cris6h16.Constants;

public final class EmailContent {


    public static String getSignUpHtmlBody(String token) {
        return HTML_SIGNUP_TEMPLATE
                .replace("{{confirmationLink}}", "http://localhost:8080/auth/verify-email?token=" + token); // todo: improve hardcoded
    }


    public static final String HTML_SIGNUP_SUBJECT = "Verify your email | cris6h16's RESTful API";
    private static final String HTML_SIGNUP_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Signup Confirmation</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        color: #333;
                        line-height: 1.6;
                    }
                    .container {
                        width: 80%;
                        margin: 0 auto;
                        padding: 20px;
                        background: #f4f4f4;
                        border-radius: 8px;
                        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                    }
                    .button {
                        display: inline-block;
                        padding: 10px 20px;
                        margin: 20px 0;
                        font-size: 16px;
                        color: #fff;
                        background-color: #007bff;
                        text-decoration: none;
                        border-radius: 5px;
                        text-align: center;
                    }
                    .button:hover {
                        background-color: #0056b3;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Welcome to cris6h16's RESTful API!</h1>
                    <p>Thank you for signing up! We're excited to have you on board.</p>
                    <p>To complete your registration, please confirm your email address by clicking the button below:</p>
                    <a href="{{confirmationLink}}" class="button">Confirm Your Email</a>
                    <p>If you did not sign up for this account, please ignore this email.</p>
                    <p>Best regards,<br>cris6h16 App</p>
                </div>
            </body>
            </html>
                        
            """;
}
