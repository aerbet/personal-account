package kg.zhaparov.personal_account.domain.model;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

public record User (
        Long id,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        @NotNull
        String phoneNumber,
        @NotNull
        String email,
        @NotNull
        String password,
        boolean verified
) {
}
