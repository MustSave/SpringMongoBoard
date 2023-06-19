package spring.mongo.board.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostForm {
    private String title;
    private String content;
}
