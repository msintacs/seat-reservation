package msintacs.seatreservation.common.web;

import jakarta.validation.Valid;
import msintacs.seatreservation.member.web.dto.SignupRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    @RestController
    @RequestMapping("/test")
    static class TestController {

        @PostMapping("/signup")
        public ResponseEntity<String> signup(@RequestBody @Valid SignupRequestDto requestDto) {
            return ResponseEntity.ok("");
        }
    }

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void 이메일_형식과_비밀번호_길이가_모두_잘못되면_두_필드의_오류를_함께_반환한다() throws Exception {

        String requestBody = """
                {
                    "email": "invalid-email",
                    "password": "Aa1!aaa"
                }
                """;

        mockMvc.perform(post("/test/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[?(@.field == 'email')].errorCode", contains("INVALID_FORMAT")))
                .andExpect(jsonPath("$.errors[?(@.field == 'password')].errorCode", contains("INVALID_LENGTH")));
    }

    @Test
    void 비밀번호에_대문자가_없으면_형식_오류를_반환한다() throws Exception {

        String requestBody = """
                {
                    "email": "test@test.com",
                    "password": "aa123!@#"
                }
                """;

        mockMvc.perform(post("/test/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[?(@.field == 'password')].errorCode", contains("INVALID_FORMAT")));
    }

    @Test
    void 이메일이_빈_문자열이면_필수값_오류를_반환한다() throws Exception {

        String requestBody = """
                {
                    "email": "",
                    "password": "Aa123!@#"
                }
                """;

        mockMvc.perform(post("/test/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[?(@.field == 'email')].errorCode", contains("REQUIRED")));
    }
}



