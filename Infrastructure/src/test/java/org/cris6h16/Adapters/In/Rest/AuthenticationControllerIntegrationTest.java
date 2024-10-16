package org.cris6h16.Adapters.In.Rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.cris6h16.Adapters.In.Rest.DTOs.CreateAccountDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginResponseDTO;
import org.cris6h16.Adapters.Out.SpringData.UserJpaRepository;
import org.cris6h16.Config.SpringBoot.Main;
import org.cris6h16.Config.SpringBoot.Properties.ControllerProperties;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.cris6h16.Utils.ErrorMessages;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = Main.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthenticationControllerIntegrationTest {

    @Mock
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ControllerProperties controllerProperties;

    @Autowired
    private ErrorMessages errorMessages;

    @Autowired
    private JwtUtilsImpl jwtUtilsImpl;

    // aviod send real emails
    @MockBean
    private JavaMailSender mailSender;

    String newPassword = "newPassword123456789";

    String accessToken;

    CreateAccountDTO created;

    static boolean setUpIsDone = false;
    private String verificationToken;

    @BeforeEach
    void setUp() {
        if (setUpIsDone) return;
        userJpaRepository.deleteAll();
    }

    @Test
    @Order(1)
    void signUp() throws Exception {
        created = createAccountDTO();
        String path = getSignupPath();

        String location = mockMvc.perform(post(path)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(created)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");


        assertEquals(location, controllerProperties.getUser().getCore());
        verify(mailSender).send(any(MimeMessage.class)); /* just check mocks */
    }

    @Test
    @Order(2)
    void login_afterCreationHasUnverifiedEmail() throws Exception {
        String path = getLoginPath();
        LoginDTO dto = toLoginDTO(created);

        mockMvc.perform(post(path)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(HttpStatus.UNPROCESSABLE_ENTITY.toString()))
                .andExpect(jsonPath("$.message").value(errorMessages.getEmailNotVerifiedMessage()));

        AtomicReference<String> capturedToken = new AtomicReference<>();

        // just check mocks ( if it happens send email again - use case )
        verify(mailSender).send(argThat((MimeMessage mimeMsg) -> {
            try {
                capturedToken.set(getToken(mimeMsg));
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        })); /* just check mocks */

        this.verificationToken = capturedToken.get();
    }

    /*
    html ..........
    <a href="https://www.example.com?token=tkn123" class="button">Confirm Your Email</a>
    html ..........
     */
    private String getToken(MimeMessage mimeMsg) throws MessagingException, IOException {
        String content = mimeMsg.getContent().toString(); //html

        // token=<anyString>
        Pattern pattern = Pattern.compile("token=([\\w-]+)");  // token pattern in the URL
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1);  // return the token value
        }

        throw new IllegalArgumentException("Token not found in the email content");
    }

    @Test
    @Order(3)
    void verifyEmail() throws Exception {
        String path = getVerifyEmailPath();
        HttpHeaders headers = bearerTokenHeader(verificationToken);
        mockMvc.perform(put(path)
                        .headers(headers))
                .andExpect(status().isOk());
    }

    private HttpHeaders bearerTokenHeader(String verificationToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + verificationToken);
        return headers;
    }

    @Test
    @Order(4)
    void login_afterVerification() throws Exception {
        String path = getLoginPath();
        LoginDTO dto = toLoginDTO(created);

        String body = mockMvc.perform(post(path)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.accessToken").isString())
                .andReturn().getResponse().getContentAsString();

        tokensAreValid(body);
        this.accessToken = getAccessTokenFromBody(body);
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
        assertTrue(jwtUtilsImpl.validate(loginResponseDTO.accessToken()));
        assertTrue(jwtUtilsImpl.validate(loginResponseDTO.refreshToken()));
    }

    @Test
    @Order(5)
    void requestPasswordReset() throws Exception {
        String path = getRequestResetPassword();
        HttpHeaders headers = bearerTokenHeader(accessToken);

        mockMvc.perform(post(path)
                        .headers(headers))
                .andExpect(status().isAccepted());
        verify(mailSender).send(any(MimeMessage.class)); /* just check mocks */
    }

    @Test
    @Order(6)
    void resetPassword() throws Exception {
        String path = getResetPassword();
        HttpHeaders headers = bearerTokenHeader(accessToken);

        mockMvc.perform(patch(path)
                        .headers(headers)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString("newPassword", newPassword)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    void login_afterPasswordReset() throws Exception {
        String path = getLoginPath();
        LoginDTO dto = new LoginDTO(created.getEmail(), newPassword);

        String body = mockMvc.perform(post(path)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string(CONTENT_TYPE, APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.accessToken").isString())
                .andReturn().getResponse().getContentAsString();

        tokensAreValid(body);
    }


    private String asJsonString(String name, String value) {
        return String.format("{\"%s\":\"%s\"}", name, value);
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
