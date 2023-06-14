package spring.mongo.board.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import spring.mongo.board.dto.LoginForm;
import spring.mongo.board.dto.ResponseMessage;
import spring.mongo.board.service.AuthService;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth")
    public ResponseEntity<ResponseMessage> login(LoginForm form, HttpServletResponse response) {
        ResponseMessage responseMessage;
        HttpStatus statusCode;

        boolean login = authService.login(form, response);
        if (login) {
            responseMessage = new ResponseMessage("로그인 성공");
            statusCode = HttpStatus.OK;
        } else {
            responseMessage = new ResponseMessage("ID 또는 비밀번호 값을 확인해주세요.");
            statusCode = HttpStatus.UNAUTHORIZED;
        }

        return new ResponseEntity<>(responseMessage, null, statusCode);
    }

    @DeleteMapping("/auth")
    public ResponseEntity<ResponseMessage> logout(@CookieValue(name = "sessionId", defaultValue = "") String sessionId) {
        if (sessionId.isEmpty()) {
            return new ResponseEntity<>(new ResponseMessage("OK"), null, HttpStatus.OK);
        }
        authService.logout(sessionId);
        return new ResponseEntity<>(new ResponseMessage("OK"), null, HttpStatus.OK);
    }
}
