package org.cris6h16.Adapters.In.Rest.DTOs;

import lombok.EqualsAndHashCode;
import org.cris6h16.In.Results.GetPublicProfileOutput;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;


@EqualsAndHashCode
public class PublicProfileDTO {

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
        this.lastModified = formatDate(output.getLastModified());
    }

    private String formatDate(Long epochInstant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return Instant.ofEpochMilli(epochInstant)
                .atZone(ZoneId.systemDefault())
                .format(formatter);
    }
}
