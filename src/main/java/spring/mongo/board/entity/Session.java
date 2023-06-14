package spring.mongo.board.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "sessions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Session {
    @Id
    private String id;
    private String userId;

    public Session(String userId, LocalDateTime creationTime) {
        this.userId = userId;
        this.creationTime = creationTime;
    }

    @Indexed(expireAfterSeconds = 1800) // TTL index for 30 min
    private LocalDateTime creationTime;
}
