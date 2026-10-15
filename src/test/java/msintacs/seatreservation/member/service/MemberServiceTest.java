package msintacs.seatreservation.member.service;


import msintacs.seatreservation.common.exception.BusinessException;
import msintacs.seatreservation.common.web.dto.ErrorCode;
import msintacs.seatreservation.member.domain.LoginInfo;
import msintacs.seatreservation.member.domain.LoginType;
import msintacs.seatreservation.member.repository.LoginInfoRepository;
import msintacs.seatreservation.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LoginInfoRepository loginInfoRepository;

    private static final String EMAIL = "member-service@test.com";
    private static final String PASSWORD = "Aa123!@#";

    @AfterEach
    void tearDown() {

        Optional<LoginInfo> optionalLoginInfo = loginInfoRepository.findByEmailAndLoginType(EMAIL, LoginType.NORMAL);

        if (optionalLoginInfo.isPresent()) {

            LoginInfo loginInfo = optionalLoginInfo.get();

            loginInfoRepository.delete(loginInfo);
            memberRepository.deleteById(loginInfo.getId());
        }
    }

    @Test
    void 동일한_이메일_계정이_일반가입을_동시에_시도하면_하나만_성공한다() throws Exception {

        // [준비] 스레드 2개 / 신호 장치 2개
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readySignal = new CountDownLatch(2);
        CountDownLatch startSignal = new CountDownLatch(1);
        long beforeRowCount = memberRepository.count();

        Callable<Void> signupTask = () -> {
            readySignal.countDown();
            startSignal.await();
            memberService.signup(EMAIL, PASSWORD);
            return null;
        };

        // [동시 실행]
        Future<Void> first = executor.submit(signupTask);
        Future<Void> second = executor.submit(signupTask);
        readySignal.await();
        log("둘 다 도착 → 출발 신호 보냄");
        startSignal.countDown();

        // [결과]
        ErrorCode errorCode = null;
        int successCount = 0;
        int failCount = 0;

        try {
            first.get();
            successCount++;
        } catch (ExecutionException e) {
            log("실패 원인: " + e.getCause().getCause().toString());
            errorCode = ((BusinessException) e.getCause()).getErrorCode();
            failCount++;
        }

        try {
            second.get();
            successCount++;
        } catch (ExecutionException e) {
            log("실패 원인: " + e.getCause().getCause().toString());
            errorCode = ((BusinessException) e.getCause()).getErrorCode();
            failCount++;
        }

        log("성공 " + successCount + "건, 실패 " + failCount + "건");

        long afterRowCount = memberRepository.count();

        // [검증]
        assertThat(successCount).isEqualTo(1);
        assertThat(afterRowCount).isEqualTo(beforeRowCount + 1);
        assertThat(errorCode).isEqualTo(ErrorCode.DUPLICATE_EMAIL);

        // [정리]
        executor.shutdown();
    }

    private static void log(String message) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + message);
    }
}