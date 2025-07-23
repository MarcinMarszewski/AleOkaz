package pl.aleokaz.backend.friends;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import pl.aleokaz.backend.friends.commands.SendFriendRequestCommand;
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
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public List<FriendRequest> getIncomingFriendRequests(UUID userId) {
        return friendRequestRepository.findAllByRecieverId(userId);
    }

    public List<FriendRequest> getSentFriendRequests(UUID userId) {
        return friendRequestRepository.findAllBySenderId(userId);
    }

    public FriendRequest sendFriendRequest(UUID senderId, UUID recieverId){
        if (senderId.equals(recieverId)) {
            throw new IllegalArgumentException("You cannot send a friend request to yourself.");
        }
        if (friendRequestRepository.existsBySenderIdAndRecieverId(senderId, recieverId)) {
            throw new IllegalArgumentException("Friend request already sent.");
        }
        if (friendshipRepository.existsByUserIdAndFriendId(senderId, recieverId)) {
            throw new IllegalArgumentException("Friendship already exists.");
        }

        User sender = userService.getUserById(senderId);
        User reciever = userService.getUserById(recieverId);

        FriendRequest friendRequest = new FriendRequest(sender, reciever);
        return friendRequestRepository.save(friendRequest);
    }

    public void cancelFriendRequest(UUID senderId, UUID recieverId) {
        List<FriendRequest> friendRequest = friendRequestRepository.findAllByRecieverIdAndSenderId(senderId, recieverId);
        if (friendRequest.isEmpty()) {
            throw new IllegalArgumentException("Friend request not found.");
        }
        friendRequestRepository.delete(friendRequest.get(0));
    }

    public Friendship acceptFriendRequest(UUID senderId, UUID recieverId) {
        List<FriendRequest> friendRequest = friendRequestRepository.findAllByRecieverIdAndSenderId(senderId, recieverId);
        if (friendRequest.isEmpty()) {
            throw new IllegalArgumentException("Friend request not found.");
        }
        FriendRequest request = friendRequest.get(0);
        User sender = request.sender();
        User reciever = request.reciever();
        Friendship friendship = new Friendship(sender, reciever);
        friendRequestRepository.delete(request);
        return friendshipRepository.save(friendship);
    }

    public void denyFriendRequest(UUID senderId, UUID recieverId) {
        List<FriendRequest> friendRequest = friendRequestRepository.findAllByRecieverIdAndSenderId(senderId, recieverId);
        if (friendRequest.isEmpty()) {
            throw new IllegalArgumentException("Friend request not found.");
        }
        FriendRequest request = friendRequest.get(0);
        friendRequestRepository.delete(request);
    }

    public List<User> getFriends(UUID currentUserId) {
        List<Friendship> friendships = friendshipRepository.findAllByUserId(currentUserId);
        return friendships.stream()
                .map(friendship -> friendship.getFriendId(currentUserId))
                .map(userService::getUserById)
                .toList();
    }

    public List<User> getFriendsOfUser(String username,UUID currentUserId) {
        //TODO: Privacy settings check
        User user = userService.getUserByUsername(username);
        return getFriends(user.id());
    }

    public void removeFriend(UUID currentUserId, UUID frindId){
        Optional<Friendship> friendship = friendshipRepository.findSymmetricalFriendship(currentUserId, frindId);
        if (friendship.isEmpty()) {
            throw new IllegalArgumentException("No friendship to remove.");
        }
        friendshipRepository.delete(friendship.get());
    }

    public List<FriendDTO> usersAsFriendDtos(List<User> users) {
        return users.stream()
                .map(user -> FriendDTO.builder()
                        .username(user.username())
                        .avatar_url(user.profilePicture())
                        .build())
                .toList();
    }

    public List<FriendDTO> friendRequestsAsFriendDtos(List<FriendRequest> friendRequests, UUID currentUserId) {
        return friendRequests.stream()
                .map(friendRequest -> friendRequest.toFriendDTO(currentUserId))
                .toList();
    }
}
