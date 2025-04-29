package ua.oleksii.realestatebroker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.model.User.Role;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByRefreshToken(String refreshToken);
    boolean existsByEmail(String email);

    long countByRole(Role role);
    long countByCreatedAtAfter(LocalDateTime dateTime);

    @Query("""
          SELECT CAST(u.createdAt AS date) AS d, COUNT(u)
          FROM User u
          WHERE u.createdAt >= :since
          GROUP BY CAST(u.createdAt AS date)
        """)
    List<Object[]> countNewUsersByDay(@Param("since") LocalDateTime since);
}
