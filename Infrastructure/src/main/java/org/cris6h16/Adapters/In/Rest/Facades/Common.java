package org.cris6h16.Adapters.In.Rest.Facades;

import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.springframework.security.core.context.SecurityContextHolder;

// package protected, common only for facades
class Common {
    protected static Long getPrincipalId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ((UserDetailsWithId) principal).getId();

        } catch (ClassCastException e) {
            throw new IllegalStateException("Principal is not an instance of UserDetailsWithId");
        }
    }

}
