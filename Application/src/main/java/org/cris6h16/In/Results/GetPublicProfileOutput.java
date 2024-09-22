package org.cris6h16.In.Results;

import org.cris6h16.Models.ERoles;

import java.util.Set;

public class GetPublicProfileOutput {
    private Long id;
    private String username;
    //    private String password;
    private String email;
    private Set<ERoles> roles;
    private Boolean active;
    private Boolean emailVerified;
    private Long lastModified;

    public GetPublicProfileOutput(Long id, String username, String email, Set<ERoles> roles, Boolean active, Boolean emailVerified, Long lastModified) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.active = active;
        this.emailVerified = emailVerified;
        this.lastModified = lastModified;
    }

    public static class Builder {
        private Long id;
        private String username;
        private String email;
        private Set<ERoles> roles;
        private Boolean active;
        private Boolean emailVerified;
        private Long lastModified;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder lastModified(Long lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder roles(Set<ERoles> roles) {
            this.roles = roles;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder emailVerified(boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }

        public GetPublicProfileOutput build(){
            return new GetPublicProfileOutput(
                    id,
                    username,
                    email,
                    roles,
                    active,
                    emailVerified,
                    lastModified
            );
        }
    }
}
