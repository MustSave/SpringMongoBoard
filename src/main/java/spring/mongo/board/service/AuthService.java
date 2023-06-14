package spring.mongo.board.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring.mongo.board.dto.LoginForm;
import spring.mongo.board.entity.Member;
import spring.mongo.board.entity.Session;
import spring.mongo.board.repository.MemberRepository;
import spring.mongo.board.repository.SessionRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final SessionRepository sessionRepository;
    private final MemberRepository memberRepository;


    public boolean login(LoginForm form, String sessionId, HttpServletResponse response) {
        Optional<Member> member = memberRepository.findMemberByUsername(form.getId());
        if (member.isEmpty()) return false;

        // 로그인 정보 비교
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(form.getPwd(), member.get().getPwd());

        // 로그인 성공 시 세션 쿠키 생성 및 전달
        if (matches) {
            Session session = sessionRepository.save(new Session(member.get().getId(), LocalDateTime.now()));
            Cookie cookie = new Cookie("sessionId", session.getId());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            response.addCookie(cookie);
        }
        
        return matches;
    }
}
