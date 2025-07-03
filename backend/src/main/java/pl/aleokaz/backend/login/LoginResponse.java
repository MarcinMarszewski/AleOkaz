package pl.aleokaz.backend.login;

import lombok.*;

@Builder
public record LoginResponse(
    @NonNull String accessToken,
    @NonNull String refreshToken)
{}
