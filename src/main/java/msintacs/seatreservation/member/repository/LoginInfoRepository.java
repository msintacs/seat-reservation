package msintacs.seatreservation.member.repository;

import msintacs.seatreservation.member.domain.LoginInfo;
import msintacs.seatreservation.member.domain.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginInfoRepository extends JpaRepository<LoginInfo, Long> {

    boolean existsByEmailAndLoginType(String email, LoginType loginType);

    Optional<LoginInfo> findByEmailAndLoginType(String email, LoginType loginType);
}
