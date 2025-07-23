package pl.aleokaz.backend.friends;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {
    List<FriendRequest> findAllBySenderId(UUID senderId);
    List<FriendRequest> findAllByRecieverId(UUID recieverId);
    boolean existsBySenderIdAndRecieverId(UUID senderId, UUID recieverId);
    List<FriendRequest> findAllByRecieverIdAndSenderId(UUID recieverId, UUID senderId);
}
