package msintacs.seatreservation.member.web.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SignupRequestDtoTest {

    @Test
    void 유효한_회원가입_요청은_검증을_통과한다() {

        SignupRequestDto request = new SignupRequestDto("test@test.com", "Aa123!@#");

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            var violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"aa123!@#", "AA123!@#", "Aa123123"})
    void 비밀번호의_필수_문자_조합이_누락되면_검증에_실패한다(String password) {

        SignupRequestDto request = new SignupRequestDto("test@test.com", password);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            var violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Aa123!@#", "Aa123!@#Aa123!@#Aa123!@#Aa123!@#"})
    void 비밀번호_길이가_허용된_경계값이면_검증을_통과한다(String password) {

        SignupRequestDto request = new SignupRequestDto("test@test.com", password);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            var violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Aa123!@", "Aa123!@#Aa123!@#Aa123!@#Aa123!@#A"})
    void 비밀번호_길이가_허용_범위를_벗어나면_검증에_실패한다(String password) {

        SignupRequestDto request = new SignupRequestDto("test@test.com", password);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            var violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 비밀번호가_null이거나_빈_문자열이거나_공백뿐이면_검증에_실패한다(String password) {

        SignupRequestDto request = new SignupRequestDto("test@test.com", password);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            var violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Aa123!@# ", "Aa123!@#한글"})
    void 비밀번호에_허용되지_않은_문자가_포함되면_검증에_실패한다(String password) {

        SignupRequestDto request = new SignupRequestDto("test@test.com", password);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            var violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"test", "@test", "test@"})
    void 이메일_형식이_올바르지_않은_경우_검증에_실패한다(String email) {

        SignupRequestDto request = new SignupRequestDto(email, "Aa123!@#");

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            var violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 이메일이_null이거나_빈_문자열이거나_공백뿐이면_검증에_실패한다(String email) {

        SignupRequestDto request = new SignupRequestDto(email, "Aa123!@#");

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            var violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
        }
    }

    @Test
    void 이메일의_길이가_허용된_경계값이면_검증을_통과한다() {

        SignupRequestDto request = new SignupRequestDto("1234567890123456789012345678901234567test@test.com", "Aa123!@#");

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            var violations = validator.validate(request);

            assertThat(violations).isEmpty();
        }
    }

    @Test
    void 이메일의_길이가_허용_범위를_벗어나면_검증에_실패한다() {

        SignupRequestDto request = new SignupRequestDto("12345678901234567890123456789012345678test@test.com", "Aa123!@#");

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            var violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
        }
    }
}