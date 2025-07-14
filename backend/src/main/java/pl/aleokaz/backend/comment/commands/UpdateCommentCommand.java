package pl.aleokaz.backend.comment.commands;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class UpdateCommentCommand {
    @NonNull
    private UUID commentId;

    @NonNull
    @NotBlank
    private String content;
}
