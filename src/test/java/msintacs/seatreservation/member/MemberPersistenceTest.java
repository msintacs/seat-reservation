package msintacs.seatreservation.member;

import jakarta.persistence.EntityManager;
import msintacs.seatreservation.member.domain.LoginInfo;
import msintacs.seatreservation.member.domain.Member;
import msintacs.seatreservation.member.repository.LoginInfoRepository;
import msintacs.seatreservation.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class MemberPersistenceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LoginInfoRepository loginInfoRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 회원과_로그인정보를_저장한다() {

        Member member = memberRepository.save(new Member());
        LoginInfo loginInfo = loginInfoRepository.save(
                LoginInfo.forNormal("test@email.com", "1234", member)
        );

        entityManager.flush();
        entityManager.clear();

        member = memberRepository.findById(member.getId()).get();
        loginInfo = loginInfoRepository.findById(loginInfo.getId()).get();

        assertThat(member.getId()).isEqualTo(loginInfo.getId());
        assertThat(loginInfo.getEmail()).isEqualTo("test@email.com");
        assertThat(loginInfo.getPassword()).isEqualTo("1234");
        assertThat(loginInfo.getMember()).isEqualTo(member);
    }

}
