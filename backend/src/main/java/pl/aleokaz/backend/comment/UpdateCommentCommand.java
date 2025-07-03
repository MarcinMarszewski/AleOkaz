package pl.aleokaz.backend.comment;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class UpdateCommentCommand {
    private UUID commentId;

    @NonNull
    private String content;
}
