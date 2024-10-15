package org.cris6h16.Adapters.In.Rest.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateMyPasswordDTO implements Serializable {
    private String currentPassword;
    private String newPassword;
}
