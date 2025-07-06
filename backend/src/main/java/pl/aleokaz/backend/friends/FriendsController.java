package pl.aleokaz.backend.friends;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import pl.aleokaz.backend.friends.commands.FriendCommand;
import pl.aleokaz.backend.security.AuthenticationService;
import pl.aleokaz.backend.util.ResponseMsgDTO;

//TODO: Add error handling with @ControllerAdvice
@RestController
@RequestMapping("/api/friends")
public class FriendsController {
    @Autowired
    private FriendsService friendsService;
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/all")
    public ResponseEntity<List<FriendDTO>> getFriends(Authentication authentication) {
        UUID currentUserId = authenticationService.getCurrentUserId(authentication);
        return ResponseEntity.ok().body(friendsService.getFriends(currentUserId));
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<FriendDTO>> getIncomingRequests(Authentication authentication) {
        UUID currentUserId = authenticationService.getCurrentUserId(authentication);
        return ResponseEntity.ok().body(friendsService.getIncomingRequests(currentUserId));
    }

    @GetMapping("/allof/{username}")
    public ResponseEntity<List<FriendDTO>> getFriendsOfUser(@PathVariable String username){
        return ResponseEntity.ok().body(friendsService.getFriendsOfUser(username));
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseMsgDTO> addFriend(Authentication authentication, @RequestBody FriendCommand addFriendCommand) {
        UUID currentUserId = authenticationService.getCurrentUserId(authentication);
        FriendsService.FriendStatus status = friendsService.addFriend(addFriendCommand, currentUserId);
        return ResponseEntity.ok().body(ResponseMsgDTO.builder().message(status.name()).build());
    }

    @PostMapping("/remove")
    public ResponseEntity<ResponseMsgDTO> removeFriends(Authentication authentication, @RequestBody FriendCommand removeFriendCommand) {
        UUID currentUserId = authenticationService.getCurrentUserId(authentication);
        FriendsService.FriendStatus status =  friendsService.removeFriend(removeFriendCommand, currentUserId);
        return ResponseEntity.ok().body(ResponseMsgDTO.builder().message(status.name()).build());
    }

    @PostMapping("/deleterequest")
    public ResponseEntity<ResponseMsgDTO> deleteFriendRequest(Authentication authentication, @RequestBody FriendCommand removeFriendCommand) {
        UUID currentUserId = authenticationService.getCurrentUserId(authentication);
        FriendsService.FriendStatus status =  friendsService.deleteFriendRequest(removeFriendCommand, currentUserId);
        return ResponseEntity.ok().body(ResponseMsgDTO.builder().message(status.name()).build());
    }
}
