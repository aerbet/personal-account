package kg.zhaparov.personal_account.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;


@Table(name = "users", schema = "public")
@Entity
@Data
public class UserEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "user_phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "user_email", nullable = false)
    private String email;

    @Column(name = "user_password", nullable = false)
    private String password;

    public UserEntity() {
    }

    public UserEntity(Long id, String phoneNumber, String email, String password) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
