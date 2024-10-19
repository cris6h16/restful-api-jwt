package org.cris6h16.In.Results;

import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;

import java.time.LocalDateTime;
import java.util.Set;

public class GetPublicProfileOutput {
    private Long id;
    private String username;
    private String email;
    private Set<ERoles> roles;
    private Boolean active;
    private Boolean emailVerified;
    private LocalDateTime lastModified;

    public GetPublicProfileOutput() { // required for deserialization / serialization
    }
    public GetPublicProfileOutput(UserModel um) {
        this.id = um.getId();
        this.username = um.getUsername();
        this.email = um.getEmail();
        this.roles = um.getRoles();
        this.active = um.getActive();
        this.emailVerified = um.getEmailVerified();
        this.lastModified = um.getLastModified();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Set<ERoles> getRoles() {
        return roles;
    }

    public Boolean getActive() {
        return active;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public LocalDateTime  getLastModified() {
        return lastModified;
    }

}
