package msintacs.seatreservation.member.repository;

import msintacs.seatreservation.member.domain.LoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginInfoRepository extends JpaRepository<LoginInfo, Long> {

}
