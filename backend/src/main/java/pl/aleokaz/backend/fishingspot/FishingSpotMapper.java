package pl.aleokaz.backend.fishingspot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.aleokaz.backend.interaction.InteractionMapper;
import pl.aleokaz.backend.post.PostDTO;

import java.util.ArrayList;

@Service
public class FishingSpotMapper {
    @Autowired InteractionMapper interactionMapper;

    public FishingSpotDto convertFishingSpotToFishingSpotDto(FishingSpot fishingSpot) {
        var postDtos = new ArrayList<PostDTO>();

        for (final var post : fishingSpot.posts()) {
            postDtos.add(post.asPostDTO());
        }

        return FishingSpotDto.builder()
            .id(fishingSpot.id())
            .name(fishingSpot.name())
            .description(fishingSpot.description())
            .ownerId(fishingSpot.owner().id())
            .latitude(fishingSpot.location().getY())
            .longitude(fishingSpot.location().getX())
            .posts(postDtos)
            .build();
    }
}
