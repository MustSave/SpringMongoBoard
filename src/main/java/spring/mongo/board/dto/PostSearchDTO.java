package spring.mongo.board.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class PostSearchDTO {
    private SearchType searchTypeEnum;
    private String query;

    @Min(0) @Nullable
    private Integer pageNumber;

    public enum SearchType {
        TITLE_ONLY, TITLE_OR_CONTENT, AUTHOR
    }
}
