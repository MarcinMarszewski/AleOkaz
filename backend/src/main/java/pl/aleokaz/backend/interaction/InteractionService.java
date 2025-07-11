package pl.aleokaz.backend.interaction;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.aleokaz.backend.interaction.exceptions.InteractionNotFoundException;

@Service
public class InteractionService {
    @Autowired
    private InteractionRepository interactionRepository;

    public Interaction getInteractionById(UUID interactionId) {
        return interactionRepository.findById(interactionId)
                .orElseThrow(() -> new InteractionNotFoundException("Interaction not found with ID: " + interactionId));
    }

    public void saveInteraction(Interaction interaction) {
        interactionRepository.save(interaction);
    }
}
