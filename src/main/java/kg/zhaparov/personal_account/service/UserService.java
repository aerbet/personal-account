package kg.zhaparov.personal_account.service;

import kg.zhaparov.personal_account.domain.mapper.UserMapper;
import kg.zhaparov.personal_account.domain.model.User;
import kg.zhaparov.personal_account.payload.request.RegisterRequest;
import kg.zhaparov.personal_account.payload.response.RegisterResponse;
import kg.zhaparov.personal_account.repository.UserRepository;
import kg.zhaparov.personal_account.repository.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository repository;
    private final NotificationService notificationService;
    private final OtpService otpService;
    private final UserMapper mapper;

    public UserService(UserRepository repository, NotificationService notificationService, OtpService otpService, UserMapper mapper) {
        this.repository = repository;
        this.notificationService = notificationService;
        this.otpService = otpService;
        this.mapper = mapper;
    }

    public List<User> findAll() {
        List<UserEntity> users = repository.findAll();
        log.info("users: {}", users);

        return users.stream()
                .map(mapper::toDomain)
                .toList();
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return repository.existsByPhoneNumber(phoneNumber);
    }

    public User createUser(User userToCreate) {
        var entityToSave = mapper.toEntity(userToCreate);

        var savedEntity = repository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    public RegisterResponse register(RegisterRequest request) {
        UserEntity existingUser = repository.findByPhoneNumber(request.getPhoneNumber());

        if (existingUser != null && existingUser.isVerified()) {
            throw new RuntimeException("User already exists");
        }

        UserEntity user = UserEntity.builder()
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        String otp = otpService.generateOtp(request.getPhoneNumber());
        log.info("client: {}, otp: {}", request.getPhoneNumber(), otp);
        UserEntity savedUser = repository.save(user);
        notificationService.sendVerificationEmail(savedUser.getEmail(), otp);

        return RegisterResponse.builder()
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .build();
    }

    public void verify(String phoneNumber, String otp) {
        UserEntity user = repository.findByPhoneNumber(phoneNumber);

        if (user == null) {
            throw new RuntimeException("User not found");
        } else if (otp.equals(otpService.getOtp(phoneNumber))) {
            user.setVerified(true);
            repository.save(user);
        } else {
            throw new RuntimeException("Bad request");
        }
    }

    public User login(String phoneNumber, String password) {
        UserEntity user = repository.findByPhoneNumber(phoneNumber);
        if (user != null && user.isVerified() && user.getPassword().equals(password)) {
            return mapper.toDomain(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
