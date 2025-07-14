package pl.aleokaz.backend.friends;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;

import pl.aleokaz.backend.friends.commands.FriendCommand;
import pl.aleokaz.backend.user.User;
import pl.aleokaz.backend.user.UserRole;
import pl.aleokaz.backend.user.UserService;
import pl.aleokaz.backend.user.exceptions.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FriendsServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private FriendsService friendsService;

    private User mockUser;
    private User mockFriend;
    private UUID userId;
    private UUID friendId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        friendId = UUID.randomUUID();
        mockUser = new User(userId, "user@mail.com", "testUser",
                "password123", new HashSet<UserRole>(), "pictureUrl");
        mockFriend = new User(friendId, "friend@mail.com", "friendUser",
                "password123", new HashSet<UserRole>(), "pictureUrl");

        when(userService.getUserById(userId)).thenReturn(mockUser);
        when(userService.getUserByUsername("testUser")).thenReturn(mockUser);
        when(userService.getUserById(friendId)).thenReturn(mockFriend);
        when(userService.getUserByUsername("friendUser")).thenReturn(mockFriend);

        when(kafkaTemplate.send(any(String.class), any(String.class)))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void shouldAddFriendWhenFriendshipDoesNotExist() throws UserNotFoundException {
        when(friendshipRepository.findSymmetricalFriendship(mockUser.id(), mockFriend.id()))
                .thenReturn(Optional.empty());
        when(friendshipRepository.save(any(Friendship.class)))
                .thenReturn(new Friendship(mockUser, mockFriend, false));

        FriendCommand friendCommand = FriendCommand.builder().username("friendUser").build();
        var result = friendsService.addFriend(friendCommand, userId);

        assertThat(result).isEqualTo(FriendsService.FriendStatus.SENT_FRIEND_REQUEST);
        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    void shouldNotAddFriendWhenSameUser() throws UserNotFoundException {
        when(friendshipRepository.findSymmetricalFriendship(mockUser.id(), mockUser.id()))
                .thenReturn(Optional.empty());

        FriendCommand friendCommand = FriendCommand.builder().username("testUser").build();
        var result = friendsService.addFriend(friendCommand, userId);

        assertThat(result).isEqualTo(FriendsService.FriendStatus.TRIED_TO_ADD_YOURSELF);
        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    void shouldRemoveFriendWhenFriendshipExists() throws UserNotFoundException {
        Friendship friendship = new Friendship(mockUser, mockFriend, true);
        when(friendshipRepository.findSymmetricalFriendship(mockUser.id(), mockFriend.id()))
                .thenReturn(Optional.of(friendship));

        FriendCommand friendCommand = FriendCommand.builder().username("friendUser").build();
        var result = friendsService.removeFriend(friendCommand, userId);

        assertThat(result).isEqualTo(FriendsService.FriendStatus.FRIEND_REMOVED);
        verify(friendshipRepository).delete(friendship);
    }

    @Test
    void shouldReturnNoFriendshipWhenRemovingUnknownFriend() throws UserNotFoundException {
        when(friendshipRepository.findSymmetricalFriendship(mockUser.id(), mockFriend.id()))
                .thenReturn(Optional.empty());

        FriendCommand friendCommand = FriendCommand.builder().username("friendUser").build();
        var result = friendsService.removeFriend(friendCommand, userId);

        assertThat(result).isEqualTo(FriendsService.FriendStatus.NO_FRIENDSHIP_TO_REMOVE);
        verify(friendshipRepository, never()).delete(any(Friendship.class));
    }

    @Test
    void shouldNotAcceptFriendRequestWhenFriendshipExists() throws UserNotFoundException {
        Friendship friendship = new Friendship(mockUser, mockFriend, true);
        when(friendshipRepository.findSymmetricalFriendship(mockUser.id(), mockFriend.id()))
                .thenReturn(Optional.of(friendship));

        FriendCommand friendCommand = FriendCommand.builder().username("friendUser").build();
        var result = friendsService.addFriend(friendCommand, userId);

        assertThat(result).isEqualTo(FriendsService.FriendStatus.FRIENDSHIP_EXISTS);
        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    void shouldAcceptFriendRequestWhenPresent() throws UserNotFoundException {
        Friendship friendship = new Friendship(mockFriend, mockUser, false);
        when(friendshipRepository.findSymmetricalFriendship(userId, friendId))
                .thenReturn(Optional.of(friendship));
        when(friendshipRepository.save(any(Friendship.class)))
                .thenReturn(new Friendship(mockUser, mockFriend, true));

        FriendCommand friendCommand = FriendCommand.builder().username("friendUser").build();
        var result = friendsService.addFriend(friendCommand, userId);

        assertThat(result).isEqualTo(FriendsService.FriendStatus.ACCEPTED_FRIEND_REQUEST);
        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    void shouldDoNothingWhenTryingToSendFriendRequestAgain() throws UserNotFoundException {
        Friendship friendship = new Friendship(mockUser, mockFriend, false);
        when(friendshipRepository.findSymmetricalFriendship(mockUser.id(), mockFriend.id()))
                .thenReturn(Optional.of(friendship));

        FriendCommand friendCommand = FriendCommand.builder().username("friendUser").build();
        var result = friendsService.addFriend(friendCommand, userId);

        assertThat(result).isEqualTo(FriendsService.FriendStatus.ALREADY_SENT_FRIEND_REQUEST);
        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    void shouldDoNothingWhenFriendshipAlreadyAccepted() throws UserNotFoundException {
        Friendship friendship = new Friendship(mockFriend, mockUser, true);
        when(friendshipRepository.findSymmetricalFriendship(mockUser.id(), mockFriend.id()))
                .thenReturn(Optional.of(friendship));

        FriendCommand friendCommand = FriendCommand.builder().username("friendUser").build();
        var result = friendsService.addFriend(friendCommand, userId);

        assertThat(result).isEqualTo(FriendsService.FriendStatus.FRIENDSHIP_ALREADY_ACCEPTED);
        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    void shouldRemoveFriendRequestWhenExists() throws UserNotFoundException {
        Friendship friendship = new Friendship(mockUser, mockFriend, false);
        when(friendshipRepository.findSymmetricalFriendship(mockUser.id(), mockFriend.id()))
                .thenReturn(Optional.of(friendship));
        doNothing().when(friendshipRepository).delete(any(Friendship.class));

        FriendCommand friendCommand = FriendCommand.builder().username("friendUser").build();
        var result = friendsService.deleteFriendRequest(friendCommand, userId);

        assertThat(result).isEqualTo(FriendsService.FriendStatus.REQUEST_DELETED);
        verify(friendshipRepository).delete(friendship);
    }

    @Test
    void shouldDoNothingWhenNoFriendRequestToDelete() throws UserNotFoundException {
        when(friendshipRepository.findSymmetricalFriendship(mockUser.id(), mockFriend.id()))
                .thenReturn(Optional.empty());

        FriendCommand friendCommand = FriendCommand.builder().username("friendUser").build();
        var result = friendsService.deleteFriendRequest(friendCommand, userId);

        assertThat(result).isEqualTo(FriendsService.FriendStatus.REQUEST_NOT_FOUND);
        verify(friendshipRepository, never()).delete(any(Friendship.class));
    }

    @Test
    void shouldGetEmptyFriendsListWhenNoFriends() {
        when(friendshipRepository.findAllByUserId(userId)).thenReturn(List.of());
        var friends = friendsService.getFriends(userId);
        assertThat(friends).isEmpty();
    }

    @Test
    void shouldGetFriendsList() {
        Friendship friendship = new Friendship(mockUser, mockFriend, true);
        when(friendshipRepository.findAllByUserId(userId)).thenReturn(List.of(friendship));

        var friends = friendsService.getFriends(userId);
        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).username()).isEqualTo("friendUser");
    }

    @Test
    void shouldGetIncomingRequests() {
        Friendship friendship = new Friendship(mockFriend, mockUser, false);
        when(friendshipRepository.findAllByUserId(userId)).thenReturn(List.of(friendship));

        var incomingRequests = friendsService.getIncomingRequests(userId);
        assertThat(incomingRequests).hasSize(1);
        assertThat(incomingRequests.get(0).username()).isEqualTo("friendUser");
    }

    @Test
    void shouldGetFriendsOfUser() {
        Friendship friendship = new Friendship(mockUser, mockFriend, true);
        when(friendshipRepository.findAllByUserId(mockFriend.id())).thenReturn(List.of(friendship));

        var friendsOfUser = friendsService.getFriendsOfUser("friendUser");
        assertThat(friendsOfUser).hasSize(1);
        assertThat(friendsOfUser.get(0).username()).isEqualTo("testUser");
    }
}