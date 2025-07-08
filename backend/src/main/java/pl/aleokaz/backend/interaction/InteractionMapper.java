package pl.aleokaz.backend.interaction;

import pl.aleokaz.backend.reaction.Reaction;
import pl.aleokaz.backend.reaction.ReactionsDto;
import pl.aleokaz.backend.user.User;

import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class InteractionMapper {
    public ReactionsDto convertReactionsToReactionsDto(Set<Reaction> reactions, User user) {
        final var result = new ReactionsDto();

        for (final var reaction : reactions) {
            if (reaction.author().equals(user)) {
                result.userReaction(reaction.type());
            }

            switch (reaction.type()) {
                case LIKE -> result.likes(result.likes() + 1);
            }
        }

        return result;
    }
}
