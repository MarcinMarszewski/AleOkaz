package pl.aleokaz.backend.post.commands;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@Builder
public class PostCommand {
    @NonNull
    private String content;

    @NonNull
    private UUID fishingSpotId;
}
