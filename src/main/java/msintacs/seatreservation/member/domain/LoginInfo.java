package msintacs.seatreservation.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "LOGIN_INFO",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_EMAIL_LOGIN_TYPE", columnNames = {"EMAIL", "LOGIN_TYPE"}),
                @UniqueConstraint(name = "UK_GOOGLE_SUB", columnNames = {"GOOGLE_SUB"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginInfo {

    @Id
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "EMAIL", nullable = false, length = 50)
    private String email;

    @Column(name = "PASSWORD", length = 100)
    private String password;

    @Column(name = "LOGIN_TYPE", nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    private LoginType loginType;

    @Column(name = "GOOGLE_SUB", length = 100)
    private String googleSub;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private LoginInfo(String email, String password, LoginType loginType, String googleSub, Member member) {
        this.email = email;
        this.password = password;
        this.loginType = loginType;
        this.googleSub = googleSub;
        this.member = member;
    }

    public static LoginInfo forNormal(String email, String password, Member member) {
        return new LoginInfo(
                email,
                password,
                LoginType.NORMAL,
                null,
                member
        );
    }

    public static LoginInfo forGoogle(String email, String googleSub, Member member) {
        return new LoginInfo(
                email,
                null,
                LoginType.GOOGLE,
                googleSub,
                member
        );
    }
}
