package kg.zhaparov.personal_account.service;

import kg.zhaparov.personal_account.repository.UserRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {
    private final StringRedisTemplate redisTemplate;
    private final UserRepository repository;

    public OtpService(StringRedisTemplate redisTemplate, UserRepository repository) {
        this.redisTemplate = redisTemplate;
        this.repository = repository;
    }


    public String generateOtp(String phoneNumber) {
        if (!repository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Client not found with phone number: " + phoneNumber);
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        String key = "otp:" + phoneNumber;
        redisTemplate.opsForValue().set(key, otp, 2, TimeUnit.MINUTES);
        return otp;
    }

    public boolean validateOtp(String phoneNumber, String otp) {
        String key = "otp:" + phoneNumber;
        String savedOtp = redisTemplate.opsForValue().get(key);
        return otp.equals(savedOtp);
    }

    public String getOtp(String phoneNumber) {
        String key = "otp:" + phoneNumber;
        return redisTemplate.opsForValue().get(key);
    }
}
