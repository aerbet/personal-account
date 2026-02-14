package kg.zhaparov.personal_account.repository;

import kg.zhaparov.personal_account.domain.model.User;
import kg.zhaparov.personal_account.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    User findByPhoneNumber(String phoneNumber);
}
