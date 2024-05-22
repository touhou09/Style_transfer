package style_transfer.transfer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import style_transfer.transfer.repository.User;
import style_transfer.transfer.repository.UserRepository;
import style_transfer.transfer.JwtUtil;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public User getUserByToken(String token) {
        String userEmail = jwtUtil.extractUserEmail(token);
        return userRepository.findByEmail(userEmail);
    }
}
