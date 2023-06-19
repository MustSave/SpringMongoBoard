package spring.mongo.board.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {
    private String id;
    private String pwd;
}
