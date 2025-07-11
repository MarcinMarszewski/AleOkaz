package pl.aleokaz.backend.fishingspot.commands;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class FishingSpotCommand {
    @NonNull
    private String name;

    private String description;

    private double latitude;
    private double longitude;
}
