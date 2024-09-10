package org.cris6h16.Adapters.In.Rest.DTOs;

public class CreateAccountDTO {
    private String username;
    private String password;
    private String email;

    public CreateAccountDTO(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public CreateAccountDTO() {
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

}
