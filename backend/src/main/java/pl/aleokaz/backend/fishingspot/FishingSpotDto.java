package pl.aleokaz.backend.fishingspot;

import lombok.Builder;
import lombok.NonNull;
import pl.aleokaz.backend.post.PostDto;

import java.util.List;
import java.util.UUID;

@Builder
public record FishingSpotDto(
    @NonNull UUID id,
    @NonNull String name,
    String description,
    @NonNull UUID ownerId,
    @NonNull double longitude,
    @NonNull double latitude,
    @NonNull List<PostDto> posts
) {}
