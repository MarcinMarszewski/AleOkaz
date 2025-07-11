package pl.aleokaz.backend.fishingspot;

import lombok.Builder;
import lombok.NonNull;
import pl.aleokaz.backend.post.PostDTO;

import java.util.List;
import java.util.UUID;

@Builder
public record FishingSpotDto(
    @NonNull UUID id,
    @NonNull String name,
    String description,
    @NonNull UUID ownerId,
    double longitude,
    double latitude,
    @NonNull List<PostDTO> posts
) {}
