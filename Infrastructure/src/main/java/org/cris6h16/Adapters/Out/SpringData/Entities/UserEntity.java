package org.cris6h16.Adapters.Out.SpringData.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.cris6h16.Models.ERoles;
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
//@EqualsAndHashCode // i cant use this annotation, I cannot put this same annotation in ERoles ( domain ), also I might create a ERoles copy exclusively for use in this Entity, only for be able to put @EqualsAndHashCode in that copy of ERole created exclusively for be able to use this annotation... That will require more custom mappings from ERoleWithLombok to ERole ( in domain ), but I wanna avoid more mappings jjj ( laughs ), I recommend do it, but I will make my own override of equals method.
@ToString
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
    @EqualsAndHashCode.Exclude
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
            columnDefinition = "timestamp(0)"
    )
    private LocalDateTime lastModified;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof UserEntity other)) return false;

        if (!Objects.equals(other.id, this.id)) return false;
        if (!Objects.equals(other.username, this.username)) return false;
        if (!Objects.equals(other.password, this.password)) return false;
        if (!Objects.equals(other.email, this.email)) return false;
        if (!Objects.equals(other.emailVerified, this.emailVerified)) return false;
        if (!Objects.equals(other.active, this.active)) return false;
        if (!Objects.equals(other.roles, this.roles)) return false;
        if (!Objects.equals(other.lastModified, this.lastModified)) return false;

        return true;
    }
}
