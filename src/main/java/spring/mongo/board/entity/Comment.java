package spring.mongo.board.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @DBRef
    private Member writer;
    private String content;
    private List<Comment> replies = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Comment(Member writer, String content) {
        this.writer = writer;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = createdAt;
    }

    public void delete() {
        this.writer = null;
        this.content = "삭제된 댓글입니다.";
        this.modifiedAt = LocalDateTime.now();
    }

    public void update(String content) {
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }
}