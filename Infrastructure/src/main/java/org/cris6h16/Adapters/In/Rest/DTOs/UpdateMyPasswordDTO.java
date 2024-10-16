package org.cris6h16.Adapters.In.Rest.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateMyPasswordDTO  {
    private String currentPassword;
    private String newPassword;
}
