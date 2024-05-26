package style_transfer.transfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.User;
import style_transfer.transfer.repository.UserRepository;
import style_transfer.transfer.repository.generatedImageResponseDto;
import style_transfer.transfer.service.UserService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

public class UserControllerTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddProjectToUser() {
        // Arrange
        User user = User.builder()
                .email("test@example.com")
                .name("Test User")
                .projects(Collections.emptyList())
                .build();

        generatedImageResponseDto project = new generatedImageResponseDto();
        project.setProjectId("project1");

        given(userRepository.findByEmail(anyString())).willReturn(user);
        given(userRepository.save(user)).willReturn(user);

        // Act
        Mono<User> result = userService.addProjectToUser("test@example.com", project);

        // Assert
        User updatedUser = result.block();
        assertEquals(1, updatedUser.getProjects().size());
        assertEquals("project1", updatedUser.getProjects().get(0).getProjectId());
    }
}
