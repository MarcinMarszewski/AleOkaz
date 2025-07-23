package pl.aleokaz.backend.friends.commands;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class SendFriendRequestCommand {
    private Void _emptyField;

    @NonNull
    @NotBlank
    private String username;
}
