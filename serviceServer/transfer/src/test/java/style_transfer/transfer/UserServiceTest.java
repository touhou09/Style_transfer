package style_transfer.transfer;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import style_transfer.transfer.repository.User;
import style_transfer.transfer.repository.UserRepository;
import style_transfer.transfer.service.UserService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testSaveOrUpdateUser() {
        String email = "test@example.com";
        String name = "Test User";

        User user = User.builder()
                .email(email)
                .name(name)
                .projects(Collections.emptyList())
                .build();

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        User savedUser = userService.saveOrUpdateUser(user).block();

        assertNotNull(savedUser);
        assertEquals(email, savedUser.getEmail());
        assertEquals(name, savedUser.getName());
    }

    @Test
    public void testGetUserByEmail() {
        String email = "test@example.com";
        String name = "Test User";

        User user = User.builder()
                .email(email)
                .name(name)
                .projects(Collections.emptyList())
                .build();

        Mockito.when(userRepository.findByEmail(email)).thenReturn(user);
        User foundUser = userService.getUserByEmail(email).block();

        assertNotNull(foundUser);
        assertEquals(email, foundUser.getEmail());
        assertEquals(name, foundUser.getName());
    }
}
