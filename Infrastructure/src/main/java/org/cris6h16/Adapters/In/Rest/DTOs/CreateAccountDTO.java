package org.cris6h16.Adapters.In.Rest.DTOs;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateAccountDTO {
    private String username;
    private String password;
    private String email;
}
