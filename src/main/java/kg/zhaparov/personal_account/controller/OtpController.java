package kg.zhaparov.personal_account.controller;


import jakarta.validation.Valid;
import kg.zhaparov.personal_account.domain.model.User;
import kg.zhaparov.personal_account.payload.request.OtpRequest;
import kg.zhaparov.personal_account.payload.request.RegisterRequest;
import kg.zhaparov.personal_account.service.OtpService;
import kg.zhaparov.personal_account.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @GetMapping
    public ResponseEntity<String> getUserOtp(@RequestBody @Valid RegisterRequest request) {
        try {
            String otp = otpService.getOtp(request.getPhoneNumber());
            if (otp == null) {
                throw new IllegalArgumentException("User don't have otp");
            }
            return new ResponseEntity<>(otp, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateOtp(
            @RequestBody OtpRequest request
    ) {
        String otp = otpService.generateOtp(request.getPhoneNumber());
        return new ResponseEntity<>(otp, HttpStatus.OK);
    }
}
