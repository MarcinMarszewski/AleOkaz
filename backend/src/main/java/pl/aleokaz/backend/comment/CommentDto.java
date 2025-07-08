package pl.aleokaz.backend.comment;

import lombok.Builder;
import lombok.NonNull;
import pl.aleokaz.backend.reaction.ReactionsDto;

import java.util.Set;
import java.util.Date;
import java.util.UUID;

@Builder
public record CommentDTO(
        @NonNull UUID id,
        @NonNull String content,
        @NonNull Date createdAt,
        Date editedAt,
        @NonNull UUID authorId,
        @NonNull ReactionsDto reactions,
        @NonNull Set<CommentDTO> comments) {
}
