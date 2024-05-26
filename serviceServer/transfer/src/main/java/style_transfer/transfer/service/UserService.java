package style_transfer.transfer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import style_transfer.transfer.repository.User;
import style_transfer.transfer.repository.UserRepository;
import style_transfer.transfer.repository.generatedImageResponseDto;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Mono<User> saveOrUpdateUser(User user) {
        return Mono.fromCallable(() -> {
            User existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser != null) {
                existingUser.setName(user.getName());
                if (user.getProjects() != null) {
                    existingUser.setProjects(user.getProjects());
                }
                return userRepository.save(existingUser);
            } else {
                return userRepository.save(user);
            }
        });
    }

    public Mono<User> getUserByEmail(String email) {
        return Mono.fromCallable(() -> userRepository.findByEmail(email));
    }

    public Mono<User> addProjectToUser(String email, generatedImageResponseDto project) {
        return getUserByEmail(email)
                .flatMap(user -> {
                    user.getProjects().add(project);
                    return saveOrUpdateUser(user);
                });
    }

    public Mono<User> getUserByToken(String token) {
        return Mono.fromCallable(() -> userRepository.findByToken(token));
    }
}
