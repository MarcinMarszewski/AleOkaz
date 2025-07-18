package pl.aleokaz.backend.friends;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import pl.aleokaz.backend.user.User;

//TODO: rework way friendship is represented in database

@Entity
@Table(name = "user_friends")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;

    @Column(name = "is_active")
    private boolean isActive;

    public UUID getFriendId(UUID currentUserId) {
        UUID userID = user().id();
        UUID friendID = friend().id();
        return userID.equals(currentUserId) ? friendID : userID;
    }

    public FriendDTO toFriendDTO(UUID currentUserId) {
        boolean isSender = user().id().equals(currentUserId);
        User other = isSender ? friend() : user();
        return FriendDTO.builder()
                .id(other.id())
                .username(other.username())
                .avatar_url(other.profilePicture())
                .is_accepted(isActive())
                .is_sender(isSender)
                .build();
    }

    public Friendship(User user, User friend, boolean isActive) {
        this.user = user;
        this.friend = friend;
        this.isActive = isActive;
    }
}