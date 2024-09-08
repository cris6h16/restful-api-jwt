package org.cris6h16.Models;

import java.util.Set;

class UserModel {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Set<ERoles> roles;
}
