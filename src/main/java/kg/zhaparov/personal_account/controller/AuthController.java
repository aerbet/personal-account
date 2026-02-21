package kg.zhaparov.personal_account.controller;

import jakarta.validation.Valid;
import kg.zhaparov.personal_account.domain.model.User;
import kg.zhaparov.personal_account.payload.request.LoginRequest;
import kg.zhaparov.personal_account.payload.request.OtpRequest;
import kg.zhaparov.personal_account.payload.request.VerificationRequest;
import kg.zhaparov.personal_account.payload.request.RegisterRequest;
import kg.zhaparov.personal_account.payload.response.RegisterResponse;
import kg.zhaparov.personal_account.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = service.findAll();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/phone")
    public ResponseEntity<User> getUserByPhoneNumber(
            @RequestParam String phoneNumber
    ) {
        User user = service.findByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(user);
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

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest request
    ) {
        RegisterResponse response = service.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(
            @RequestBody VerificationRequest request
    ) {
        try {
            service.verify(request.getPhoneNumber(), request.getOtp());
            return new ResponseEntity<>("User verified successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
            ) {
        User user = service.login(request.getPhoneNumber(), request.getPassword());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
