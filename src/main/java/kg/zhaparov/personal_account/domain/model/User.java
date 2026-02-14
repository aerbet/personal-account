package kg.zhaparov.personal_account.domain.model;


import jakarta.validation.constraints.NotNull;

public record User (
        Long id,
        @NotNull
        String phoneNumber,
        @NotNull
        String email,
        @NotNull
        String password
) {
}
