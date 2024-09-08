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
}
