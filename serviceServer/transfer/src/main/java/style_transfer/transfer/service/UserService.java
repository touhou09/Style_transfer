package style_transfer.transfer.service;

import style_transfer.transfer.repository.User;

public interface UserService {
    User getUserByToken(String token);
}