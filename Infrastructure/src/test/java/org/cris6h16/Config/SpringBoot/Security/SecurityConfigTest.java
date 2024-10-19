package org.cris6h16.Config.SpringBoot.Security;

import org.cris6h16.Adapters.In.Rest.Facades.AuthenticationControllerFacade;
import org.cris6h16.Adapters.In.Rest.Facades.UserAccountControllerFacade;
import org.cris6h16.Adapters.In.Rest.UserAccountController;
import org.cris6h16.Config.SpringBoot.Controllers.CustomControllerExceptionHandler;
import org.cris6h16.Config.SpringBoot.Properties.ControllerProperties;
import org.cris6h16.Config.SpringBoot.Properties.JwtProperties;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.cris6h16.Utils.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Configuration
@ComponentScan(basePackageClasses = {
        UserAccountController.class,
        CustomControllerExceptionHandler.class,
        SecurityConfig.class,
        JwtUtilsImpl.class,
        JwtProperties.class
})
@ActiveProfiles("test")
class ControllersWithAdviceAndSecurity {
}

@SpringBootTest(classes = {ControllersWithAdviceAndSecurity.class})
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
@Tag("with-spring-context")
public class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private ControllerProperties controllerProperties;

    @MockBean
    private UserAccountControllerFacade userAccountControllerFacade;

    @MockBean
    private AuthenticationControllerFacade authenticationControllerFacade;

    @MockBean
    private ErrorMessages errorMessages;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // the facades can be mocked otpionally if i want change the status
    }


    @Nested
    class UnauthenticatedTests {
        @Test
        void testPermitAllPaths() throws Exception {
            String[] posts = postPermitAllPaths();
            int expectedStatus = HttpStatus.OK.value();

            for (String path : posts) performAndAssert("POST", path, expectedStatus);
        }

        @Test
        void testNotPermitAllPaths() throws Exception {
            int expectedStatus = HttpStatus.FORBIDDEN.value();

            String[] gets = concat(getAndAdminPaths(), getAndUserPaths());
            String[] posts = concat(postAndUserPaths(), postAndAuthenticated());
            String[] puts = putAndUserPath();
            String[] patches = patchAndUserPaths();
            String[] deletes = deleteAndUserPath();

            for (String path : gets) performAndAssert("GET", path, expectedStatus);
            for (String path : posts) performAndAssert("POST", path, expectedStatus);
            for (String path : puts) performAndAssert("PUT", path, expectedStatus);
            for (String path : patches) performAndAssert("PATCH", path, expectedStatus);
            for (String path : deletes) performAndAssert("DELETE", path, expectedStatus);
        }


    }

    @Nested
    @WithMockUser(roles = "USER")
    class UserRolesTests {

        @Test
        void testForbiddenPaths() throws Exception {
            int expectedStatus = HttpStatus.FORBIDDEN.value();
            String[] gets = securityConfig.getAndAdminPaths();

            for (String path : gets) performAndAssert("GET", path, expectedStatus);
        }

        @Test
        void testAuthorizedPaths() throws Exception {
            int expectedStatus = HttpStatus.OK.value();
            String[] gets = getAndUserPaths();
            String[] posts = concat(postAndUserPaths(), postPermitAllPaths(), postAndAuthenticated());
            String[] puts = putAndUserPath();
            String[] patches = patchAndUserPaths();
            String[] deletes = deleteAndUserPath();

            for (String path : gets) performAndAssert("GET", path, expectedStatus);
            for (String path : posts) performAndAssert("POST", path, expectedStatus);
            for (String path : puts) performAndAssert("PUT", path, expectedStatus);
            for (String path : patches) performAndAssert("PATCH", path, expectedStatus);
            for (String path : deletes) performAndAssert("DELETE", path, expectedStatus);
        }

    }

    private String[] concat(String[]... strings) {
        return Arrays.stream(strings).flatMap(Arrays::stream).toArray(String[]::new); // { {}, {} } -> { }
    }


    @Nested
    @WithMockUser(roles = "ADMIN")
    class AdminRolesTests {

        @Test
        void testForbiddenPaths() throws Exception {
            int expectedStatus = HttpStatus.FORBIDDEN.value();
            String[] gets = getAndUserPaths();
            String[] posts = postAndUserPaths();
            String[] puts = putAndUserPath();
            String[] patches = patchAndUserPaths();
            String[] deletes = deleteAndUserPath();

            for (String path : gets) performAndAssert("GET", path, expectedStatus);
            for (String path : posts) performAndAssert("POST", path, expectedStatus);
            for (String path : puts) performAndAssert("PUT", path, expectedStatus);
            for (String path : patches) performAndAssert("PATCH", path, expectedStatus);
            for (String path : deletes) performAndAssert("DELETE", path, expectedStatus);
        }

        @Test
        void testAuthorizedPaths() throws Exception {
            int expectedStatus = HttpStatus.OK.value();
            String[] gets = concat(getAndAdminPaths());
            String[] posts = concat(postPermitAllPaths(), postAndAuthenticated());

            for (String path : gets) performAndAssert("GET", path, expectedStatus);
            for (String path : posts) performAndAssert("POST", path, expectedStatus);
        }
    }


    String[] getAndAdminPaths() {
        return securityConfig.getAndAdminPaths();
    }

    String[] getAndUserPaths() {
        return securityConfig.getAndUserPaths();
    }

    String[] postPermitAllPaths() {
        return securityConfig.postPermitAllPaths();
    }

    String[] postAndUserPaths() {
        return securityConfig.postAndUserPaths();
    }

    String[] postAndAuthenticated() {
        return securityConfig.postAndAuthenticated();
    }

    String[] putAndUserPath() {
        return securityConfig.putAndUserPaths();
    }

    String[] patchAndUserPaths() {
        return securityConfig.patchAndUserPaths();
    }

    String[] deleteAndUserPath() {
        return securityConfig.deleteAndUserPath();
    }


    void performAndAssert(String method, String path, int expectedStatus) throws Exception {
        MockHttpServletRequestBuilder request = switch (method) {
            case "POST" -> post(path);
            case "PUT" -> put(path);
            case "PATCH" -> patch(path);
            case "DELETE" -> delete(path);
            case "GET" -> get(path);
            default -> throw new IllegalArgumentException();
        };

        MediaType mediaType = getMediaType(path);

        System.out.println("Performing " + method + " request to " + path + " with media type " + mediaType);

        int status = mockMvc.perform(request
                        .contentType(mediaType)
                        .content("{}"))
                .andReturn().getResponse().getStatus();

        assertEquals(status, expectedStatus);
    }

    private MediaType getMediaType(String path) {
        // paths that are TEXT_PLAIN
        String[] textPlain = new String[]{
                "/api/v1/auth/request-reset-password",
                "/api/v1/user/account/update/username",
                "/api/v1/user/account/update/email",
                "/api/v1/auth/password"
        };
        for (String s : textPlain) {
            if (path.equals(s)) return MediaType.TEXT_PLAIN;
        }
        return MediaType.APPLICATION_JSON;
    }


}