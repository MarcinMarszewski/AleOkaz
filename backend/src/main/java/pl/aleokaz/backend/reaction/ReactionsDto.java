package pl.aleokaz.backend.reaction;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReactionsDto {
    @JsonProperty
    private ReactionType userReaction;

    @JsonProperty
    private long likes = 0;
}
