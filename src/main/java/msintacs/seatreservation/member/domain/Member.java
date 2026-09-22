package msintacs.seatreservation.member.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "MEMBER")
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "ROLE", nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    private MemberRole role;

    public Member() {
        this.role = MemberRole.USER;
    }
}
