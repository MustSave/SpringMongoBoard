package spring.mongo.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupForm {
    private String username;
    private String pwd;
    private String nickname;
}
