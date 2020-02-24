package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.UserEntity;
import pl.mkjb.exchange.model.UserModel;
import pl.mkjb.exchange.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isGivenUserNameAlreadyUsed(UserModel userModel) {
        return userRepository.findByUsername(userModel.getUserName())
                .map(userEntity -> userModel.getId() != userEntity.getId())
                .orElse(false);
    }

    public void save(UserModel userModel) {
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        val userEntity = UserEntity.fromModel(userModel);
        userRepository.save(userEntity);
    }
}
