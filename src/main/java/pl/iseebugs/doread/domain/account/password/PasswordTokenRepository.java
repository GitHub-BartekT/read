package pl.iseebugs.doread.domain.account.password;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.iseebugs.doread.domain.account.create.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> {

    Optional<PasswordToken> findByToken (String token);

}
