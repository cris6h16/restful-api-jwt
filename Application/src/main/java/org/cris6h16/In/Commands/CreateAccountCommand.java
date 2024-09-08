package org.cris6h16.In.Commands;

public class CreateAccountCommand {
    private String username;
    private String password;
    private String email;

    public CreateAccountCommand(String username, String password, String email) {
//        todo: add setter validations as not null / blank / eyc
        this.setUsername(username);
        this.setPassword(password);
        this.setEmail(email);
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
