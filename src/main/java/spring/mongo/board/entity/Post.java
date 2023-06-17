package spring.mongo.board.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;
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
    @DBRef
    private PostComment comments;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Post(Member writer, String title, String content) {
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = createdAt;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }

    public PostComment getComments() {
        return this.comments;
    }

    public void setComments(PostComment comments) {
        this.comments = comments;
    }
}
