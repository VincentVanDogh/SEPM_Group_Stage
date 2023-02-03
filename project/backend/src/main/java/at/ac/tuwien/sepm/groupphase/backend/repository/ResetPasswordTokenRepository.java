package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    /**
     * Find ResetPasswordToken that includes string token.
     *
     * @param token the string token that is looked for
     *
     * @return Optional ResetPasswordToken that matches the given token
     */
    Optional<ResetPasswordToken> findByToken(String token);
}
