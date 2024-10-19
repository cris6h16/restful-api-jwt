package org.cris6h16.Adapters.In.Rest.DTOs;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.cris6h16.In.Results.GetPublicProfileOutput;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;


@EqualsAndHashCode
@NoArgsConstructor
@Slf4j
@ToString
@Getter
@Setter
public class PublicProfileDTO {

    private String id;
    private String username;
    private String email;
    private String roles;
    private Boolean active;
    private Boolean emailVerified;
    private LocalDateTime lastModified;


    public PublicProfileDTO(GetPublicProfileOutput output) {
        this.id = output.getId().toString();
        this.username = output.getUsername();
        this.email = output.getEmail();
        this.roles = output.getRoles().toString();
        this.active = output.getActive();
        this.emailVerified = output.getEmailVerified();
        this.lastModified = output.getLastModified();
        log.trace("PublicProfileDTO built: {}", this.toString());
    }
}
