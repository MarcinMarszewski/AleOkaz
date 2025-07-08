package pl.aleokaz.backend.comment;

import pl.aleokaz.backend.interaction.Interaction;
import pl.aleokaz.backend.reaction.Reaction;
import pl.aleokaz.backend.reaction.ReactionsDto;
import pl.aleokaz.backend.user.User;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Comment extends Interaction {
    @NonNull
    @ManyToOne(optional = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Interaction parent;

    @Builder
    public Comment(
            UUID id,
            @NonNull Interaction parent,
            @NonNull String content,
            @NonNull Date createdAt,
            Date editedAt,
            User author,
            Set<Reaction> reactions,
            Set<Comment> comments) {
        super(id, content, createdAt, editedAt, author, reactions, comments);
        this.parent = parent;
    }

    public CommentDTO asCommentDto() {
        final var comments = new HashSet<CommentDTO>();
        for (final var subcomment : comments()) {
            comments.add(subcomment.asCommentDto());
        }

        final var reactionsDto = new ReactionsDto();

        for (final var reaction : reactions()) {
            if (reaction.author().equals(author())) {
                reactionsDto.userReaction(reaction.type());
            }

            switch (reaction.type()) {
                case LIKE -> reactionsDto.likes(reactionsDto.likes() + 1);
            }
        }

        return CommentDTO.builder()
                .id(id())
                .content(content())
                .createdAt(createdAt())
                .editedAt(editedAt())
                .authorId(author().id())
                .reactions(reactionsDto)
                .comments(comments)
                .build();
    }
}
