package pl.aleokaz.backend.user;

import java.net.URISyntaxException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.aleokaz.backend.register.RegisterCommand;
import pl.aleokaz.backend.image.exceptions.ImageSaveException;
import pl.aleokaz.backend.login.LoginCommand;
import pl.aleokaz.backend.login.LoginResponse;

// TODO(michalciechan): Obsługa wyjątków.
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(@RequestBody RegisterCommand registerCommand)
            throws URISyntaxException {
        final var user = userService.registerUser(registerCommand);
        final var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.id())
                .toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginCommand loginCommand) {
        return ResponseEntity.ok(userService.loginUser(loginCommand));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshUserToken(@RequestBody RefreshCommand refreshCommand) {
        return ResponseEntity.ok(userService.refreshUserToken(refreshCommand));
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserInfo(id));
    }

    @GetMapping("/info")
    public ResponseEntity<UserDto> getUserInfo(Authentication authentication) {
        UUID currentUserId = UUID.fromString((String) authentication.getPrincipal());

        return ResponseEntity.ok(userService.getUserInfo(currentUserId));
    }

    @PutMapping(path="/info", consumes = "multipart/form-data")
    public ResponseEntity<UserDto> updateUserInfo(
        Authentication authentication,
        @RequestPart(value = "userInfo", required = false) UpdateInfoCommand updateInfoCommand,
        @RequestParam(value = "image", required = false) MultipartFile image) {

        UUID currentUserId = UUID.fromString((String) authentication.getPrincipal());

        try {
            UserDto userInfo = userService.updateUserInfo(currentUserId, updateInfoCommand, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(userInfo);
        } catch (ImageSaveException pse) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        }
    }

}
