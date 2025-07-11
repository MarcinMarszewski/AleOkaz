package pl.aleokaz.backend.fishingspot.exceptions;

import java.util.UUID;

public class FishingSpotNotFoundException extends RuntimeException {
    public FishingSpotNotFoundException(UUID id) {
        super("Fishing Spot with id " + id + " not found");
    }

    public FishingSpotNotFoundException(String message) {
        super(message);
    }
    
}
