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
    private final EmailService emailService;
    private final OtpService otpService;
    private final UserMapper mapper;

    public UserService(UserRepository repository, EmailService emailService, OtpService otpService, UserMapper mapper) {
        this.repository = repository;
        this.emailService = emailService;
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

    public User createUser(User userToCreate) {
        var entityToSave = mapper.toEntity(userToCreate);

        var savedEntity = repository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    public RegisterResponse register(RegisterRequest request) {
        UserEntity existingUser = repository.findByPhoneNumber(request.getEmail());

        if (existingUser != null && existingUser.isVerified()) {
            throw new RuntimeException("User already exists");
        }

        UserEntity user = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        String otp = otpService.generateOtp(request.getPhoneNumber());
        log.info("client: {}, otp: {}", request.getPhoneNumber(), otp);
        UserEntity savedUser = repository.save(user);
        sendVerificationEmail(savedUser.getEmail(), otp);

        RegisterResponse response = RegisterResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .otp(otp)
                .build();

        return response;
    }

    public void verify(String phoneNumber, String otp) {
        UserEntity user = repository.findByPhoneNumber(phoneNumber);

        if (user == null) {
            throw new RuntimeException("User not found");
        } else if (user.isVerified()) {
            throw new RuntimeException("User is already verified");
        } else if (otp.equals(otpService.getOtp(phoneNumber))) {
            user.setVerified(true);
            repository.save(user);
        } else {
            throw new RuntimeException("Bad request");
        }
    }

    public UserEntity login(String email, String password) {
        UserEntity user = repository.findByPhoneNumber(email);
        if (user != null && user.isVerified() && user.getPassword().equals(password)) {
            return user;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void sendVerificationEmail(String email, String otp) {
        String subject = "Email verification";
        String body = "your verification code: " + otp;
        emailService.sendEmail(email, subject, body);
    }
}
