package org.cris6h16.Adapters.In.Rest;

import CommonConfigs.NoAsyncConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;
import org.cris6h16.Adapters.In.Rest.DTOs.CreateAccountDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginResponseDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.RefreshAccessTokenResponseDTO;
import org.cris6h16.Adapters.Out.SpringData.UserJpaRepository;
import org.cris6h16.Config.SpringBoot.Main;
import org.cris6h16.Config.SpringBoot.Properties.ControllerProperties;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.cris6h16.Utils.ErrorMessages;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = {Main.class, NoAsyncConfig.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("with-spring-context")
class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository; // used for debugging

    @Autowired
    private ControllerProperties controllerProperties;

    @Autowired
    private ErrorMessages errorMessages;

    @Autowired
    private JwtUtilsImpl jwtUtilsImpl;

    // aviod send real emails
    @MockBean
    private JavaMailSender mailSender;

    final CreateAccountDTO created = createAccountDTO();

    @BeforeAll
    static void setUp(@Autowired UserJpaRepository userJpaRepository) {
        userJpaRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAll();
    }

    @Test
    @Order(1)
    void signUp() throws Exception {
        String path = getSignupPath();
        MimeMessage mimeMessage = mockMime();

        String location = mockMvc.perform(post(path)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(created)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");


        assertEquals(location, controllerProperties.getUser().getCore());
        verify(mailSender).send(any(MimeMessage.class));
    }

    private MimeMessage mockMime() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        return mimeMessage;
    }

    @Test
    @Order(2)
    void verifyEmail_success() throws Exception {
        String verificationToken = createUser();
        String path = getVerifyEmailPath();
        HttpHeaders headers = bearerTokenHeader(verificationToken);
        mockMvc.perform(put(path)
                        .headers(headers))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(3)
    void login_endpoint() throws Exception {
        String verificationToken = createUser();
        verifyEmail(verificationToken);

        LoginDTO dto = toLoginDTO(created);
        MimeMessage mimeMessage = mockMime()
;
        String body = mockMvc.perform(post(getLoginPath())
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
                .andReturn().getResponse().getContentAsString();

        tokensAreValid(body);
    }

    private String createUser() throws Exception {
        clearInvocations(mailSender);
        AtomicReference<String> verificationToken = new AtomicReference<>();
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        mockMvc.perform(post(getSignupPath())
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(created)))
                .andExpect(status().isCreated());

        verify(mailSender).send(any(MimeMessage.class));
        verify(mimeMessage).setContent(argThat(multipart -> {
            try {
                verificationToken.set(getToken(multipart));
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));

        return verificationToken.get();
    }

    /*
    html ..........
    <a href="https://www.example.com?token=tkn123" class="button">Confirm Your Email</a>
    html ..........
     */
    private String getToken(Multipart multipart) throws MessagingException, IOException {
        String content = getContent(multipart);

        // token=<anyString>
        Pattern pattern = Pattern.compile("token=([^\\\"&]+)");  // token pattern in the URL
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1);  // return the token value
        }

        throw new IllegalArgumentException("Token not found in the email content");
    }

    private String getContent(Multipart multipart) throws MessagingException, IOException {
        Path file = Files.createTempFile("cris6h16", ".txt");
        try (OutputStream os = Files.newOutputStream(file)) {
            multipart.writeTo(os);
        }
        String content = Files.readString(file, StandardCharsets.UTF_8);
        System.out.println("Content: " + content);
        return content;
    }


    private HttpHeaders bearerTokenHeader(String verificationToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + verificationToken);
        return headers;
    }


    private void verifyEmail(String verificationToken) throws Exception {
        String path = getVerifyEmailPath();
        HttpHeaders headers = bearerTokenHeader(verificationToken);
        mockMvc.perform(put(path)
                        .headers(headers))
                .andExpect(status().isNoContent());
    }

    private String getAccessTokenFromBody(String body) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            LoginResponseDTO loginResponseDTO = mapper.readValue(body, LoginResponseDTO.class);
            return loginResponseDTO.accessToken();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void tokensAreValid(String body) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        LoginResponseDTO loginResponseDTO = mapper.readValue(body, LoginResponseDTO.class);
        tokenIsValid(loginResponseDTO.accessToken());
    }

    private boolean tokenIsValid(String token) {
        return jwtUtilsImpl.validate(token);
    }

    @Test
    @Order(4)
    void requestPasswordReset() throws Exception {
        String verificationToken = createUser();
        verifyEmail(verificationToken);
        String accessToken = login_accessToken();

        clearInvocations(mailSender);

        MimeMessage mimeMessage = mockMime()
;
        mockMvc.perform(post(getRequestResetPassword())
                        .headers(bearerTokenHeader(accessToken))
                        .contentType(TEXT_PLAIN_VALUE)
                        .content(created.getEmail()))
                .andExpect(status().isAccepted());

        verify(mailSender).send(any(MimeMessage.class)); /* just check mocks */
        verify(mimeMessage).setContent(argThat(multipart -> {
            try {
                return tokenIsValid(getToken(multipart));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    private String login_accessToken() {
        LoginDTO dto = toLoginDTO(created);
        try {
            String body = mockMvc.perform(post(getLoginPath())
                            .contentType(APPLICATION_JSON)
                            .content(asJsonString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.accessToken").isString())
                    .andReturn().getResponse().getContentAsString();

            return getAccessTokenFromBody(body);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String login_refreshToken() {
        LoginDTO dto = toLoginDTO(created);
        try {
            String body = mockMvc.perform(post(getLoginPath())
                            .contentType(APPLICATION_JSON)
                            .content(asJsonString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.refreshToken").isString())
                    .andReturn().getResponse().getContentAsString();

            return getRefreshTokenFromBody(body);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getRefreshTokenFromBody(String body) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            LoginResponseDTO loginResponseDTO = mapper.readValue(body, LoginResponseDTO.class);
            return loginResponseDTO.refreshToken();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(5)
    void resetPassword() throws Exception {
        String verificationToken = createUser();
        verifyEmail(verificationToken);
        String accessToken = login_accessToken();
        String resetPasswordToken = requestPasswordReset(accessToken);

        mockMvc.perform(patch(getResetPassword())
                        .headers(bearerTokenHeader(resetPasswordToken))
                        .contentType(TEXT_PLAIN_VALUE)
                        .content("newPassword123456789"))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(6)
    void refreshAccessToken() throws Exception {
        String verificationToken = createUser();
        verifyEmail(verificationToken);
        String refreshToken = login_refreshToken();

        String body = mockMvc.perform(post(refreshAccessTokenPath())
                        .headers(bearerTokenHeader(refreshToken)))
                .andExpect(status().isOk())
                .andExpect(header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.accessToken").isString())
                .andReturn().getResponse().getContentAsString();

        String accessToken = new ObjectMapper().readValue(body, RefreshAccessTokenResponseDTO.class).getAccessToken();
        tokenIsValid(accessToken);
    }

    private String refreshAccessTokenPath() {
        return controllerProperties.getAuthentication().getRefreshAccessToken();
    }

    private String requestPasswordReset(String accessToken) {
        clearInvocations(mailSender);
        AtomicReference<String> resetPasswordToken = new AtomicReference<>();
        MimeMessage mimeMessage = mockMime()
;
        try {
            mockMvc.perform(post(getRequestResetPassword())
                            .headers(bearerTokenHeader(accessToken))
                            .contentType(TEXT_PLAIN)
                            .content(created.getEmail()))
                    .andExpect(status().isAccepted());

            verify(mailSender).send(any(MimeMessage.class)); /* just check mocks */
            verify(mimeMessage).setContent(argThat(multipart -> {
                try {
                    resetPasswordToken.set(getToken(multipart));
                    return tokenIsValid(getToken(multipart));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return resetPasswordToken.get();
    }

    private String getResetPassword() {
        return controllerProperties.getAuthentication().getResetPassword();
    }


    private String getRequestResetPassword() {
        return controllerProperties.getAuthentication().getRequestResetPassword();
    }

    private String getVerifyEmailPath() {
        return controllerProperties.getAuthentication().getVerifyEmail();
    }

    private LoginDTO toLoginDTO(CreateAccountDTO created) {
        return new LoginDTO(
                created.getEmail(),
                created.getPassword()
        );
    }

    private String getLoginPath() {
        return controllerProperties.getAuthentication().getLogin();
    }


    private <T> String asJsonString(T dto) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(dto);
    }

    private String getSignupPath() {
        return controllerProperties.getAuthentication().getSignup();
    }

    private CreateAccountDTO createAccountDTO() {
        return new CreateAccountDTO(
                "cris6h16",
                "12345678",
                "cristianmherrera21@gmail.com"
        );
    }
}
