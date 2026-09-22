package msintacs.seatreservation.member.repository;

import msintacs.seatreservation.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
