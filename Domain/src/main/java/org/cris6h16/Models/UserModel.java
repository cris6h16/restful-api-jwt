package org.cris6h16.Models;

import java.util.Set;

public class UserModel {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Set<ERoles> roles;
    private Boolean active;
    private Boolean emailVerified;
    private Long lastModified;

    private UserModel(Long id, String username, String password, String email, Set<ERoles> roles, Boolean active, Boolean emailVerified, Long lastModified) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.active = active;
        this.emailVerified = emailVerified;
        this.lastModified = lastModified;
    }

    private UserModel() {
    }


    public Long getId() {
        return id;
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

    public Boolean getActive() {
        return active;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static class Builder {
        private Long id;
        private String username;
        private String password;
        private String email;
        private Set<ERoles> roles;
        private Boolean active;
        private Boolean emailVerified;
        private Long lastModified;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setRoles(Set<ERoles> roles) {
            this.roles = roles;
            return this;
        }

        public Builder setActive(Boolean active) {
            this.active = active;
            return this;
        }

        public Builder setEmailVerified(Boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }

        public Builder setLastModified(Long lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public UserModel build() {
            return new UserModel(
                    id,
                    username,
                    password,
                    email,
                    roles,
                    active,
                    emailVerified,
                    lastModified
            );
        }
    }
}

