package spring.mongo.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import spring.mongo.board.dto.SignupForm;
import spring.mongo.board.entity.Member;
import spring.mongo.board.service.MemberService;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/members")
    @ResponseBody
    public ResponseEntity<String> register(SignupForm signupForm) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Member member = new Member(
                signupForm.getUsername(),
                encoder.encode(signupForm.getPwd())
        );
        Member joinedMember = memberService.join(member);

        HttpStatus statusCode = statusCode = HttpStatus.OK;;
        String responseBody = "OK";

        if (joinedMember == null) {
            statusCode = HttpStatus.CONFLICT;
            responseBody = "Error: Duplicate key violation";
        }

        return ResponseEntity.status(statusCode).body(responseBody);
    }
}
