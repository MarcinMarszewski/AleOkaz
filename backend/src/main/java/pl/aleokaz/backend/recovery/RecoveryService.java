package pl.aleokaz.backend.recovery;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.micrometer.common.lang.NonNull;
import pl.aleokaz.backend.user.UserService;
import pl.aleokaz.backend.mail.MailingService;
import pl.aleokaz.backend.recovery.commands.CheckTokenCommand;
import pl.aleokaz.backend.recovery.commands.RecoveryCommand;
import pl.aleokaz.backend.recovery.commands.ResetPasswordCommand;
import pl.aleokaz.backend.recovery.exceptions.TokenNotFoundException;
import pl.aleokaz.backend.user.User;
import pl.aleokaz.backend.user.UserNotFoundException;

@Service
public class RecoveryService {

    @Value("${recovery.token.expiration.minutes}")
    private int tokenExpirationMinutes;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private MailingService mailingService;

    public RecoveryService() {
        super();
    }

    public void createAndSendRecoveryToken(RecoveryCommand recoveryCommand) {
        RecoveryToken recoveryToken = createRecoveryToken(recoveryCommand);

        mailingService.sendEmail(recoveryCommand.email(),
                "Recovery token",
                "Your recovery token is: " + recoveryToken.token());
    }

    public boolean verifyRecoveryToken(CheckTokenCommand checkTokenCommand)
            throws UserNotFoundException, TokenNotFoundException {
        return isTokenCorrect(checkTokenCommand.email(), checkTokenCommand.token());
    }

    public boolean resetPassword(ResetPasswordCommand resetPasswordCommand) {
        if (isTokenCorrect(resetPasswordCommand.email(), resetPasswordCommand.token())) {
            User user = userService.getUserByEmail(resetPasswordCommand.email());
            userService.setUserPassword(user, resetPasswordCommand.password());
            RecoveryToken recoveryToken = tokenRepository.findByUserId(user.id());
            tokenRepository.delete(recoveryToken);
            return true;
        }
        return false;
    }

    public RecoveryToken createRecoveryToken(RecoveryCommand recoveryCommand) {
        User user = userService.getUserByEmail(recoveryCommand.email());

        RecoveryToken recoveryToken = RecoveryToken.builder()
                .token(generateToken())
                .expirationDate(LocalDateTime.now().plusMinutes(tokenExpirationMinutes))
                .user(user)
                .build();

        RecoveryToken existingToken = tokenRepository.findByUserId(user.id());
        if (existingToken != null) {
            tokenRepository.delete(existingToken);
        }
        tokenRepository.save(recoveryToken);
        return recoveryToken;
    }

    public boolean isTokenCorrect(@NonNull String email, @NonNull String token)
            throws TokenNotFoundException {
        User user = userService.getUserByEmail(email);
        RecoveryToken recoveryToken = tokenRepository.findByUserId(user.id());
        if (recoveryToken == null) {
            throw new TokenNotFoundException("userId", user.id().toString());
        }
        if (!recoveryToken.expirationDate().isAfter(LocalDateTime.now())) {
            tokenRepository.delete(recoveryToken);
            return false;
        }
        if (recoveryToken.attempts() >= 3) {
            tokenRepository.delete(recoveryToken);
            return false;
        }
        if (recoveryToken.token().equals(token)) {
            return true;
        } else {
            recoveryToken.attempts(recoveryToken.attempts() + 1);
            tokenRepository.save(recoveryToken);
            return false;
        }
    }

    private String generateToken() {
        String token = "";
        for (int i = 0; i < 7; i++) {
            token += (char) (Math.random() * 26 + 97);
        }
        return token;
    }
}