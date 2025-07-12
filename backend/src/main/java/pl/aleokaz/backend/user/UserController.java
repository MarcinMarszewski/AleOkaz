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

import pl.aleokaz.backend.security.AuthenticationService;
import pl.aleokaz.backend.security.LoginResponse;
import pl.aleokaz.backend.security.RefreshResponse;
import pl.aleokaz.backend.user.commands.LoginCommand;
import pl.aleokaz.backend.user.commands.RefreshCommand;
import pl.aleokaz.backend.user.commands.RegisterCommand;
import pl.aleokaz.backend.user.commands.UpdateInfoCommand;

// TODO(michalciechan): Obsługa wyjątków.
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id).asUserDTO());
    }

    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterCommand registerCommand)
            throws URISyntaxException {
        User user = userService.registerUser(registerCommand.username(),
                registerCommand.email(),
                registerCommand.password());
        final var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.id())
                .toUri();
        return ResponseEntity.created(uri).body(user.asUserDTO());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginCommand loginCommand) {
        LoginResponse loginResponse = userService.loginUser(loginCommand.username(), loginCommand.password());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshUserToken(@RequestBody RefreshCommand refreshCommand) {
        RefreshResponse refreshResponse = userService.refreshUserToken(refreshCommand.refreshToken());
        return ResponseEntity.ok(refreshResponse);
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<UserDTO> getUserInfo(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user.asUserDTO());
    }

    @GetMapping("/info")
    public ResponseEntity<UserDTO> getUserInfo(Authentication authentication) {
        UUID currentUserId = authenticationService.getCurrentUserId(authentication);
        return ResponseEntity.ok(userService.getUserById(currentUserId).asUserDTO());
    }

    @PutMapping(path="/info", consumes = "multipart/form-data")
    public ResponseEntity<UserDTO> updateUserInfo(
                Authentication authentication,
                @RequestPart(value = "userInfo", required = false) UpdateInfoCommand updateInfoCommand,
                @RequestParam(value = "image", required = false) MultipartFile image) {
        UUID currentUserId = authenticationService.getCurrentUserId(authentication);
        User user = userService.updateUserInfo(currentUserId, updateInfoCommand, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(user.asUserDTO());
    }
}