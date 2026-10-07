package msintacs.seatreservation.member.service;

import msintacs.seatreservation.common.exception.BusinessException;
import msintacs.seatreservation.common.web.dto.ErrorCode;
import msintacs.seatreservation.member.domain.LoginInfo;
import msintacs.seatreservation.member.domain.LoginType;
import msintacs.seatreservation.member.domain.Member;
import msintacs.seatreservation.member.repository.LoginInfoRepository;
import msintacs.seatreservation.member.repository.MemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final LoginInfoRepository loginInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, LoginInfoRepository loginInfoRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.loginInfoRepository = loginInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void signup(String email, String password) {

        // 중복 검사
        if (loginInfoRepository.existsByEmailAndLoginType(email, LoginType.NORMAL)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 패스워드 인코딩
        String encodedPassword = passwordEncoder.encode(password);

        // MEMBER 테이블 INSERT
        Member member = memberRepository.save(new Member());

        // LOGIN_INFO 테이블 INSERT
        try {
            loginInfoRepository.saveAndFlush(LoginInfo.forNormal(email, encodedPassword, member));
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL, exception);
        }
    }
}
