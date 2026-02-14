package kg.zhaparov.personal_account.domain.mapper;

import kg.zhaparov.personal_account.domain.model.User;
import kg.zhaparov.personal_account.repository.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(
            UserEntity user
    ) {
        return new User(
                user.getId(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getPassword()
        );
    }

    public UserEntity toEntity(
            User user
    ) {
        return new UserEntity(
                user.id(),
                user.phoneNumber(),
                user.email(),
                user.password()
        );
    }
}
