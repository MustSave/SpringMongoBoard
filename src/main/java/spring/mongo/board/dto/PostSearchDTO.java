package spring.mongo.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;

import java.security.InvalidParameterException;

@Data
public class PostSearchDTO {
    private SearchType searchType;
    private String query;

    @NotNull(message = "Invalid parameter")
    private PagingType pagingType = PagingType.NEXT;

    @NotBlank(message = "Invalid parameter")
    private String basePostId = "000000000000000000000000";

    public enum SearchType {
        TITLE_ONLY, TITLE_OR_CONTENT, AUTHOR
    }

    public enum PagingType {
        PREV, NEXT
    }

    private boolean isValidPostId() {
        return basePostId.equals("000000000000000000000000") || ObjectId.isValid(basePostId);
    }

    public void validate() {
        if (!isValidPostId()) throw new InvalidParameterException("Invalid postId");
        if (searchType != null && (query == null || query.length() < 2)) throw new IllegalArgumentException("검색어를 두 글자 이상 입력해주세요");
    }
}