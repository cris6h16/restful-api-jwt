package org.cris6h16.Adapters.Out.SpringData;

import org.cris6h16.Config.SpringBoot.Main;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = Main.class)
@Transactional
public class UserJpaRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
//        userRepository.deleteAll();
    }

    @Test
    public void updateEmailVerifiedByIdCustom() {
        // Arrange
        userRepository.save(new UserModel.Builder()
                .setUsername("cris6h16")
                .setEmail("cristianmherrera21@gmail.com")
                .setPassword("12345678")
                .setActive(true)
                .setEmailVerified(true)
                .setRoles(Set.of(ERoles.ROLE_USER))
                .setLastModified(Instant.now()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime())  // Removes timezone info
                .build()
        );

        // Act
        userRepository.updateEmailVerifiedById(1L, false);

        // Assert
        UserModel userModel = userRepository.findById(1L).orElseThrow();
        assertFalse(userModel.getEmailVerified());
    }
}
