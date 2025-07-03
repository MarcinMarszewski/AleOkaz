package pl.aleokaz.backend.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateInfoCommand {
    private Void __ignore;

    private String username;
}
