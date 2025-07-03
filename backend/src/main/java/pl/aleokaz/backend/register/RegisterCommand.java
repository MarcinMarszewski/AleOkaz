package pl.aleokaz.backend.register;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public
class RegisterCommand {
        @NonNull
        @NotBlank
        private String username;

        @NonNull
        @NotBlank
        private String email;

        @NonNull
        @NotBlank
        private char[] password;
}