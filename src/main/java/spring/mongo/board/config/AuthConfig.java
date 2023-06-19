package spring.mongo.board.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import spring.mongo.board.entity.Member;
import spring.mongo.board.repository.SessionRepository;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {
    private final SessionRepository sessionRepository;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorizationMiddleware(sessionRepository))
                .addPathPatterns("/posts/**");
    }

    @RequiredArgsConstructor
    public static class AuthorizationMiddleware implements HandlerInterceptor {
        private final SessionRepository sessionRepository;

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("sessionId")) {
                        Optional<Member> member = sessionRepository.findMemberBySessionId(cookie.getValue());
                        if (member.isEmpty()) break;

                        request.setAttribute("member", member.get());
                        return true;
                    }
                }
            }

            response.setStatus(401);
            response.getWriter().println("{message:'UNAUTHORIZED'}");
            return false;
        }
    }
}
