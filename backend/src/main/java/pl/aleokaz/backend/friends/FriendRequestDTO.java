package pl.aleokaz.backend.friends;

import lombok.Builder;
import lombok.NonNull;

@Builder
record FriendRequestDTO(
        @NonNull String from,
        @NonNull String avatar_url) {
}
