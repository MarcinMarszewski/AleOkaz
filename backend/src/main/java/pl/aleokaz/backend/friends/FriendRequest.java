package pl.aleokaz.backend.friends;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import pl.aleokaz.backend.user.User;

@Entity
@Table(name = "user_friends")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User reciever;

    public FriendRequest(User sender, User reciever) {
        this.sender = sender;
        this.reciever = reciever;
    }
    
    public FriendDTO toFriendDTO(UUID currentUserId) {
        boolean isSender = sender.id().equals(currentUserId);
        User other = isSender ? reciever : sender;
        return FriendDTO.builder()
                .username(other.username())
                .avatar_url(other.profilePicture())
                .build();
    }
}
