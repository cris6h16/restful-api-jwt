package org.cris6h16.Adapters.In.Rest.DTOs;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.cris6h16.In.Results.GetPublicProfileOutput;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;


@EqualsAndHashCode
@NoArgsConstructor
@JsonSerialize
public class PublicProfileDTO implements Serializable {

    private String id;
    private String username;
    private String email;
    private String roles;
    private Boolean active;
    private Boolean emailVerified;
    private String lastModified;


    public PublicProfileDTO(GetPublicProfileOutput output) {
        this.id = output.getId().toString();
        this.username = output.getUsername();
        this.email = output.getEmail();
        this.roles = output.getRoles().stream()
               .map(eRoles -> eRoles.toString().replace("EROLE_", ""))
               .collect(Collectors.joining(", "));
        this.active = output.getActive();
        this.emailVerified = output.getEmailVerified();
        this.lastModified = String.valueOf(output.getLastModified());
    }
}
