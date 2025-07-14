package pl.aleokaz.backend.friends;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import pl.aleokaz.backend.friends.commands.FriendCommand;
import pl.aleokaz.backend.user.User;
import pl.aleokaz.backend.user.UserService;
import pl.aleokaz.backend.user.exceptions.UserNotFoundException;

@Service
public class FriendsService {

    public enum FriendStatus {
        SENT_FRIEND_REQUEST,
        ACCEPTED_FRIEND_REQUEST,
        TRIED_TO_ADD_YOURSELF,
        FRIENDSHIP_EXISTS,
        FRIENDSHIP_ALREADY_ACCEPTED,
        ALREADY_SENT_FRIEND_REQUEST,
        FRIEND_REMOVED,
        NO_FRIENDSHIP_TO_REMOVE,
        REQUEST_DELETED,
        REQUEST_NOT_FOUND
    }

    @Autowired
    private UserService userService;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public FriendStatus addFriend(FriendCommand addFriendCommand, UUID userId) throws UserNotFoundException {
        User friend = userService.getUserByUsername(addFriendCommand.username());
        User user = userService.getUserById(userId);

        if (user.id().equals(friend.id()))
            return FriendStatus.TRIED_TO_ADD_YOURSELF;

        Optional<Friendship> existingFriendship =  friendshipRepository
                .findSymmetricalFriendship(user.id(), friend.id());
        if (existingFriendship.isEmpty()) {
            friendshipRepository.save(new Friendship(user, friend, false));
            kafkaTemplate.send(friend.id().toString(), "Friend request from " + user.username());
            return FriendStatus.SENT_FRIEND_REQUEST;
        }

        Friendship friendship = existingFriendship.get();
        if (friendship.friend().id() == userId) {
            if (friendship.isActive())
                return FriendStatus.FRIENDSHIP_ALREADY_ACCEPTED;
            friendship.isActive(true);
            friendshipRepository.save(friendship);
            kafkaTemplate.send(friend.id().toString(), "Friend request accepted by " + user.username());
            return FriendStatus.ACCEPTED_FRIEND_REQUEST;
        }
        return friendship.isActive() ? FriendStatus.FRIENDSHIP_EXISTS
                : FriendStatus.ALREADY_SENT_FRIEND_REQUEST;
    }

    public FriendStatus removeFriend(FriendCommand removeFriendCommand, UUID userId) throws UserNotFoundException {
        User friend = userService.getUserByUsername(removeFriendCommand.username());
        User user = userService.getUserById(userId);

        Optional<Friendship> existingFriendship = friendshipRepository
                .findSymmetricalFriendship(user.id(),friend.id());
        if (existingFriendship.isPresent()) {
            friendshipRepository.delete(existingFriendship.get());
            kafkaTemplate.send(friend.id().toString(), "Removed from friends by " + user.username());
            return FriendStatus.FRIEND_REMOVED;
        }
        return FriendStatus.NO_FRIENDSHIP_TO_REMOVE;
    }

    public List<FriendDTO> getFriends(UUID userId) {
        return friendshipRepository.findAllByUserId(userId)
                .stream()
                .map(friendship -> friendship.toFriendDTO(userId))
                .toList();
    }

    public List<FriendDTO> getFriendsOfUser(String username) {
        User user = userService.getUserByUsername(username);
        return friendshipRepository.findAllByUserId(user.id())
                .stream()
                .filter(friendship -> friendship.isActive())
                .map(friendship -> friendship.toFriendDTO(user.id()))
                .toList();
    }

    public List<FriendDTO> getIncomingRequests(UUID userId) {
        List<Friendship> friendships = friendshipRepository.findAllByUserId(userId);
        return friendships.stream()
                .filter(friendship -> !friendship.isActive())
                .map(friendship -> friendship.toFriendDTO(userId))
                .filter(friendDTO -> !friendDTO.is_sender())
                .toList();
    }

    public FriendStatus deleteFriendRequest(FriendCommand removeFriendCommand, UUID userId)
            throws UserNotFoundException {
        User friend = userService.getUserByUsername(removeFriendCommand.username());
        User user = userService.getUserById(userId);

        Optional<Friendship> existingFriendship = friendshipRepository
                .findSymmetricalFriendship(user.id(),friend.id());
        if (existingFriendship.isPresent()) {
            friendshipRepository.delete(existingFriendship.get());
            kafkaTemplate.send(friend.id().toString(), "Friend request denied by " + user.username());
            return FriendStatus.REQUEST_DELETED;
        }
        return FriendStatus.REQUEST_NOT_FOUND;
    }
}