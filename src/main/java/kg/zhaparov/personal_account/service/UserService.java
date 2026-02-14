package kg.zhaparov.personal_account.service;

import kg.zhaparov.personal_account.domain.mapper.UserMapper;
import kg.zhaparov.personal_account.domain.model.User;
import kg.zhaparov.personal_account.repository.UserRepository;
import kg.zhaparov.personal_account.repository.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<User> findAll() {
        List<UserEntity> users = repository.findAll();
        log.info("users: {}", users);

        return users.stream()
                .map(mapper::toDomain)
                .toList();
    }

    public User createUser(User userToCreate) {
        var entityToSave = mapper.toEntity(userToCreate);

        var savedEntity = repository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }
}
