package pl.aleokaz.backend.reaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ReactionCommand {
    @NonNull
    private UUID interactionId;

    @NonNull
    private ReactionType reactionType;
}
