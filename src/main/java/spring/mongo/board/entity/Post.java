package spring.mongo.board.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Post {
    @Id
    private String id;
    @DBRef
    private Member writer;
    private String title;
    private String content;
    private List<Comment> comments = new ArrayList<>();

    public Post(Member writer, String title, String content) {
        this.writer = writer;
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Data
    public static class Comment {
//        @Id
//        private String id;
        @DBRef
        private Member writer;
        private String content;

        private List<Comment> replies = new ArrayList<>();

        public Comment(Member writer, String content) {
            this.writer = writer;
            this.content = content;
        }

        public void delete() {
            this.writer = null;
            this.content = "삭제된 댓글입니다.";
        }
    }
}
