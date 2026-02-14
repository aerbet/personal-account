package kg.zhaparov.personal_account.controller;

import jakarta.validation.Valid;
import kg.zhaparov.personal_account.domain.model.User;
import kg.zhaparov.personal_account.payload.request.OtpRequest;
import kg.zhaparov.personal_account.repository.entity.UserEntity;
import kg.zhaparov.personal_account.service.OtpService;
import kg.zhaparov.personal_account.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    private final OtpService otpService;
    private final UserService service;

    public UserController(OtpService otpService, UserService service) {
        this.otpService = otpService;
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = service.findAll();

        return ResponseEntity.ok(users);
    }

    @PostMapping("/otp/generate")
    public ResponseEntity<String> generateOtp(@RequestBody @Valid OtpRequest request) {
        try {
            String otp = otpService.generateOtp(request.getPhoneNumber());
            log.info("client: {}, otp: {}", request.getPhoneNumber(), otp);
            return ResponseEntity.ok(otp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid User user) {
        try {
            log.info("Creating user with phone: {}", user.phoneNumber());

            User createdUser = service.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            log.error("Error creating user: ", e);
            return new ResponseEntity<>((HttpHeaders) null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
