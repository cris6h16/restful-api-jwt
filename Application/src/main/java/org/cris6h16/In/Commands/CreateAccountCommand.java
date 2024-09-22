package org.cris6h16.In.Commands;

import org.cris6h16.Models.ERoles;

import java.util.Set;

public class CreateAccountCommand {
    private String username;
    private String password;
    private String email;
    private Set<ERoles> roles;

    public CreateAccountCommand(String username, String password, String email, Set<ERoles> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
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

    public Set<ERoles> getRoles() {
        return roles;
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

    public void setRoles(Set<ERoles> roles) {
        this.roles = roles;
    }
}
