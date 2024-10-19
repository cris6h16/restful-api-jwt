package org.cris6h16.Adapters.In.Rest;

import CommonConfigs.NoAsyncConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;
import org.cris6h16.Adapters.In.Rest.DTOs.CreateAccountDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.LoginResponseDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.UpdateMyPasswordDTO;
import org.cris6h16.Adapters.Out.SpringData.UserJpaRepository;
import org.cris6h16.Config.SpringBoot.Main;
import org.cris6h16.Config.SpringBoot.Properties.ControllerProperties;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.MyPasswordEncoder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {Main.class, NoAsyncConfig.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserAccountControllerIntegrationTest {


    @Autowired
    private JwtUtilsImpl jwtUtilsImpl;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MyPasswordEncoder passwordEncoder;

    @Autowired
    private ControllerProperties controllerProperties;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserRepository userRepository;

    private final CreateAccountDTO created = createAccountDTO();

    @MockBean
    private JavaMailSender mailSender;

    @BeforeAll
    static void setUp(@Autowired UserJpaRepository userJpaRepository) {
        userJpaRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAll();
    }

    @Test
    void requestDeleteMyAccount_success() throws Exception {
        createAccountExpectedState();
        String accessToken = login_accessToken();
        MimeMessage mimeMessage = mockedMime();

        mockMvc.perform(post(requestDeleteMyAccountPath())
                        .headers(bearerTokenHeader(accessToken)))
                .andExpect(status().isAccepted());

        verify(mailSender, times(1)).send(mimeMessage);
        verify(mimeMessage, times(1)).setContent(argThat(multipart -> tokenIsValid(getToken(multipart))));
    }

    @Test
    void deleteMyAccount_success() throws Exception {
        createAccountExpectedState();
        String accessToken = login_accessToken();
        String deleteAccountToken = requestDeleteMyAccount(accessToken);

        mockMvc.perform(delete(deleteMyAccountPath())
                        .headers(bearerTokenHeader(deleteAccountToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateMyUsername_success() throws Exception {
        createAccountExpectedState();
        String newUsername = "new-username";
        String accessToken = login_accessToken();

        mockMvc.perform(patch(updateMyUsernamePath())
                        .headers(bearerTokenHeader(accessToken))
                        .contentType(TEXT_PLAIN_VALUE)
                        .content(newUsername))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateMyPassword_success() throws Exception {
        createAccountExpectedState();
        String accessToken = login_accessToken();
        String newPassword = "new-password";
        UpdateMyPasswordDTO dto = new UpdateMyPasswordDTO("12345678", newPassword);

        mockMvc.perform(patch(updateMyPasswordPath())
                        .headers(bearerTokenHeader(accessToken))
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void requestUpdateMyEmail_success() throws Exception {
        createAccountExpectedState();
        String accessToken = login_accessToken();
        MimeMessage mimeMessage = mockedMime();

        mockMvc.perform(post(requestUpdateMyEmailPath())
                        .headers(bearerTokenHeader(accessToken)))
                .andExpect(status().isAccepted());

        verify(mailSender, times(1)).send(mimeMessage);
        verify(mimeMessage, times(1)).setContent(argThat(multipart -> tokenIsValid(getToken(multipart))));
    }

    @Test
    void updateMyEmail_success() throws Exception {
        createAccountExpectedState();
        String accessToken = login_accessToken();
        String updateEmailToken = requestUpdateMyEmail(accessToken);
        String newEmail = "newEail@gmail.com";

        mockMvc.perform(patch(updateMyEmailPath())
                        .headers(bearerTokenHeader(updateEmailToken))
                        .contentType(TEXT_PLAIN_VALUE)
                        .content(newEmail))
                .andExpect(status().isNoContent());
    }

    private String updateMyEmailPath() {
        return controllerProperties.getUser().getAccount().getUpdate().getEmail();
    }

    @Test
    void getMyAccount_success() throws Exception {
        createAccountExpectedState();
        String accessToken = login_accessToken();

        mockMvc.perform(get(getMyAccountPath())
                        .headers(bearerTokenHeader(accessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(created.getUsername()))
                .andExpect(jsonPath("$.email").value(created.getEmail()))
                .andExpect(jsonPath("$.roles").value("[ROLE_USER]"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.emailVerified").value(true))
                .andExpect(jsonPath("$.lastModified").isString());
    }

    @Test
    void getAllUsers_success() throws Exception {
        LocalDateTime fixedTime = LocalDateTime.now();
        createAccountExpectedState(ERoles.ROLE_ADMIN);
        createAccounts(5, fixedTime);
        String accessToken = login_accessToken();

        // I can map this to the same object, to Page or Output, but due to the time it will be left as you see
       mockMvc.perform(get(getAllUsersPath()) // default query params
                        .headers(bearerTokenHeader(accessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(6))
                .andExpect(jsonPath("$.content[5].username").value(created.getUsername()))
                .andExpect(jsonPath("$.content[5].email").value(created.getEmail()))
                .andExpect(jsonPath("$.content[5].roles").value("[ROLE_ADMIN]"))
                .andExpect(jsonPath("$.content[5].active").value(true))
                .andExpect(jsonPath("$.content[5].emailVerified").value(true))
                .andExpect(jsonPath("$.content[5].lastModified").isString())
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(50))
                .andExpect(jsonPath("$.number").value(0));
       // pd: the hardcoded values are the default values of the Pageable

        /* Body looks like:

                {
          "content": [
            {
              "id": "7",
              "username": "username4",
              "email": "email4@gmail.com",
              "roles": "[ROLE_USER]",
              "active": true,
              "emailVerified": true,
              "lastModified": "2024-10-18T17:48:08"
            },
            {
              "id": "6",
              "username": "username3",
              "email": "email3@gmail.com",
              "roles": "[ROLE_USER]",
              "active": true,
              "emailVerified": true,
              "lastModified": "2024-10-18T17:48:08"
            },
            {
              "id": "5",
              "username": "username2",
              "email": "email2@gmail.com",
              "roles": "[ROLE_USER]",
              "active": true,
              "emailVerified": true,
              "lastModified": "2024-10-18T17:48:08"
            },
            {
              "id": "4",
              "username": "username1",
              "email": "email1@gmail.com",
              "roles": "[ROLE_USER]",
              "active": true,
              "emailVerified": true,
              "lastModified": "2024-10-18T17:48:08"
            },
            {
              "id": "3",
              "username": "username0",
              "email": "email0@gmail.com",
              "roles": "[ROLE_USER]",
              "active": true,
              "emailVerified": true,
              "lastModified": "2024-10-18T17:48:08"
            },
            {
              "id": "2",
              "username": "cris6h16",
              "email": "cristianmherrera21@gmail.com",
              "roles": "[ROLE_ADMIN]",
              "active": true,
              "emailVerified": true,
              "lastModified": "2024-10-18T17:48:08"
            }
          ],
          "pageable": {
            "pageNumber": 0,
            "pageSize": 50,
            "sort": {
              "empty": false,
              "unsorted": false,
              "sorted": true
            },
            "offset": 0,
            "unpaged": false,
            "paged": true
          },
          "last": true,
          "totalPages": 1,
          "totalElements": 6,
          "size": 50,
          "number": 0,
          "sort": {
            "empty": false,
            "unsorted": false,
            "sorted": true
          },
          "first": true,
          "numberOfElements": 6,
          "empty": false
        }

         */

    }


    private void createAccounts(int n, LocalDateTime forAll) {
        for (int i = 0; i < n; i++) {
            CreateAccountDTO dto = new CreateAccountDTO(
                    "username" + i,
                    "password" + i,
                    "email" + i + "@gmail.com"
            );
            createAccountExpectedState(dto, forAll);
        }
    }

    private String getAllUsersPath() {
        return controllerProperties.getUser().getPagination().getAll();
    }

    private String getMyAccountPath() {
        return controllerProperties.getUser().getAccount().getCore();
    }

    private String requestUpdateMyEmail(String accessToken) throws Exception {
        AtomicReference<String> token = new AtomicReference<>();
        MimeMessage mimeMessage = mockedMime();
        mockMvc.perform(post(requestUpdateMyEmailPath())
                        .headers(bearerTokenHeader(accessToken)))
                .andExpect(status().isAccepted());

        verify(mimeMessage, times(1)).setContent(argThat(multipart -> {
            token.set(getToken(multipart));
            return tokenIsValid(token.get());
        }));

        return token.get();
    }

    private String requestUpdateMyEmailPath() {
        return controllerProperties.getUser().getAccount().getRequest().getUpdateEmail();
    }

    private String updateMyPasswordPath() {
        return controllerProperties.getUser().getAccount().getUpdate().getPassword();
    }

    private String updateMyUsernamePath() {
        return controllerProperties.getUser().getAccount().getUpdate().getUsername();
    }

    private String deleteMyAccountPath() {
        return controllerProperties.getUser().getAccount().getCore();
    }

    private String requestDeleteMyAccount(String accessToken) throws Exception {
        AtomicReference<String> deleteAccountToken = new AtomicReference<>();
        MimeMessage mimeMessage = mockedMime();

        mockMvc.perform(post(requestDeleteMyAccountPath())
                        .headers(bearerTokenHeader(accessToken)))
                .andExpect(status().isAccepted());

        verify(mimeMessage, times(1)).setContent(argThat(multipart -> {
            deleteAccountToken.set(getToken(multipart));
            return tokenIsValid(deleteAccountToken.get());
        }));

        return deleteAccountToken.get();
    }


    /*
 html ..........
 <a href="https://www.example.com?token=tkn123" class="button">Confirm Your Email</a>
 html ..........
  */
    private String getToken(Multipart multipart) {
        String content = getContent(multipart);

        // token=<anyString>
        Pattern pattern = Pattern.compile("token=([^\\\"&]+)");  // token pattern in the URL
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1);  // return the token value
        }

        throw new IllegalArgumentException("Token not found in the email content");
    }

    private boolean tokenIsValid(String token) {
        return jwtUtilsImpl.validate(token);
    }

    private String getContent(Multipart multipart) {
        try {
            Path file = Files.createTempFile("cris6h16", ".txt");
            try (OutputStream os = Files.newOutputStream(file)) {
                multipart.writeTo(os);
            }
            String content = Files.readString(file, StandardCharsets.UTF_8);
            System.out.println("Content: " + content);
            return content;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MimeMessage mockedMime() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        return mimeMessage;
    }

    /**
     * @return access token
     */
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

    private String getAccessTokenFromBody(String body) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            LoginResponseDTO loginResponseDTO = mapper.readValue(body, LoginResponseDTO.class);
            return loginResponseDTO.accessToken();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> String asJsonString(T dto) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(dto);
    }


    private String getLoginPath() {
        return controllerProperties.getAuthentication().getLogin();
    }


    private LoginDTO toLoginDTO(CreateAccountDTO created) {
        return new LoginDTO(
                created.getEmail(),
                created.getPassword()
        );
    }

    private HttpHeaders bearerTokenHeader(String verificationToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + verificationToken);
        return headers;
    }

    private String requestDeleteMyAccountPath() {
        return controllerProperties.getUser().getAccount().getRequest().getDelete();
    }

    private void createAccountExpectedState() {
        UserModel model = new UserModel.Builder()
                .setUsername(created.getUsername())
                .setEmail(created.getEmail())
                .setPassword(passwordEncoder.encode(created.getPassword()))
                .setActive(true)
                .setEmailVerified(true)
                .setLastModified(LocalDateTime.now())
                .setRoles(roles())
                .build();

        userRepository.save(model);
        userJpaRepository.flush();
    }

    private void createAccountExpectedState(CreateAccountDTO dto, LocalDateTime forAll) {
        UserModel model = new UserModel.Builder()
                .setUsername(dto.getUsername())
                .setEmail(dto.getEmail())
                .setPassword(passwordEncoder.encode(dto.getPassword()))
                .setActive(true)
                .setEmailVerified(true)
                .setLastModified(forAll)
                .setRoles(roles())
                .build();

        userRepository.save(model);
        userJpaRepository.flush();
    }

    private void createAccountExpectedState(ERoles... roles) {
        UserModel model = new UserModel.Builder()
                .setUsername(created.getUsername())
                .setEmail(created.getEmail())
                .setPassword(passwordEncoder.encode(created.getPassword()))
                .setActive(true)
                .setEmailVerified(true)
                .setLastModified(LocalDateTime.now())
                .setRoles(roles(roles))
                .build();

        userRepository.save(model);
        userJpaRepository.flush();
    }

    private Set<ERoles> roles(ERoles[] roles) {
        return Set.of(roles);
    }

    private Set<ERoles> roles() {
        return Set.of(ERoles.ROLE_USER);
    }

    private CreateAccountDTO createAccountDTO() {
        return new CreateAccountDTO(
                "cris6h16",
                "12345678",
                "cristianmherrera21@gmail.com"
        );
    }
}
