package spring.mongo.board.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {
    @Id @JsonIgnore
    private String id;

    // 회원 ID
    @Indexed(unique = true) @JsonIgnore
    private String username;
    @JsonIgnore
    private String pwd;

    // 회원 닉네임
    @Indexed
    private String nickname;

    public Member(String username, String pwd, String nickname) {
        this.username = username;
        this.pwd = pwd;
        this.nickname = nickname;
    }

    public Member(String id) {
        this.id=id;
    }
}
