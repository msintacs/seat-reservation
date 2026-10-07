package msintacs.seatreservation.member.web;

import jakarta.persistence.EntityManager;
import msintacs.seatreservation.member.domain.LoginInfo;
import msintacs.seatreservation.member.domain.LoginType;
import msintacs.seatreservation.member.domain.Member;
import msintacs.seatreservation.member.domain.MemberRole;
import msintacs.seatreservation.member.repository.LoginInfoRepository;
import msintacs.seatreservation.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginInfoRepository loginInfoRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 중복되지_않은_이메일과_정상_비밀번호로_가입했을때_가입이_완료된다() throws Exception {

        String email = "test@test.com";
        String password = "Aa123!@#";

        String requestBody = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated());

        entityManager.flush();
        entityManager.clear();

        LoginInfo loginInfo = loginInfoRepository.findByEmailAndLoginType(email, LoginType.NORMAL).orElseThrow();
        Member member = memberRepository.findById(loginInfo.getId()).orElseThrow();

        assertThat(passwordEncoder.matches(password, loginInfo.getPassword())).isTrue();
        assertThat(member.getRole()).isEqualTo(MemberRole.USER);
    }

    @Test
    void 이미_일반가입된_이메일로_다시_가입하면_가입이_거절된다() throws Exception {

        String email = "test@test.com";
        String password = "Aa123!@#";

        String requestBody = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);


        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated());

        long memberRowCount = memberRepository.count();

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"));

        assertThat(memberRepository.count()).isEqualTo(memberRowCount);
    }

    @Test
    void 올바르지_않은_비밀번호_형식으로_가입하면_가입이_거절된다() throws Exception {

        String email = "test@test.com";
        String password = "aa123!@#";

        String requestBody = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        long memberRowCount = memberRepository.count();

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[?(@.field == 'password')].errorCode", contains("INVALID_FORMAT")));

        assertThat(memberRepository.count()).isEqualTo(memberRowCount);
    }

    @Test
    void JSON_문법이_깨진_본문으로_가입하면_요청_형식_오류를_반환한다() throws Exception {

        String requestBody = """
                {
                    "email": "test@test.com",
                    "password": Aa123!@#
                }
                """;

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST_BODY"))
                .andExpect(jsonPath("$.message").value("요청 형식이 올바르지 않습니다."))
                .andExpect(jsonPath("$.errors", hasSize(0)));
    }
}