package org.cris6h16.Adapters.Out.SpringData.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cris6h16.Models.ERoles;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            unique = true,
            nullable = false,
            length = 20,
            name = "username"
    )
    private String username;

    @Column(
            nullable = false,
            length = 1000,
            name = "password"
    )
    private String password;

    @Column(
            unique = true,
            nullable = false,
            length = 50,
            name = "email"
    )
    private String email;

    @ElementCollection(
            targetClass = ERoles.class,
            fetch = FetchType.EAGER
    )
    @CollectionTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            foreignKey = @ForeignKey(name = "fk_users_roles_user_id")
    )
    @Enumerated(EnumType.STRING)
    private Set<ERoles> roles;

    @Column(
            nullable = false,
            name = "active",
            columnDefinition = "BOOLEAN DEFAULT false"
    )
    private Boolean active;

    @Column(
            nullable = false,
            name = "email_verified",
            columnDefinition = "BOOLEAN DEFAULT false"
    )
    private Boolean emailVerified;

    @Column(
            nullable = false,
            name = "last_modified",
            columnDefinition = "BIGINT DEFAULT 0"
    )
    private LocalDateTime lastModified;


}
