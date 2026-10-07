package msintacs.seatreservation.member.web;

import jakarta.validation.Valid;
import msintacs.seatreservation.member.service.MemberService;
import msintacs.seatreservation.member.web.dto.SignupRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Valid @RequestBody SignupRequestDto requestDto) {
        memberService.signup(requestDto.getEmail(), requestDto.getPassword());
    }
}
