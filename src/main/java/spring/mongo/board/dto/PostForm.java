package spring.mongo.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostForm {
    private String title;
    private String content;
}
