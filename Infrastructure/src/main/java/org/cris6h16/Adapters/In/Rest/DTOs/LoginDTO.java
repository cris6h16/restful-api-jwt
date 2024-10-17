package org.cris6h16.Adapters.In.Rest.DTOs;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoginDTO {
    private String email;
    private String password;
}
