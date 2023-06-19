package spring.mongo.board.api.dto;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.eclipse.osgi.signedcontent.InvalidContentException;
import org.springframework.lang.Nullable;

import java.lang.annotation.*;
import java.security.InvalidParameterException;

@Data
public class PostSearchDTO {
    private SearchType searchType;
    private String query;

    private PagingType pagingType;

    private String basePostId;

    @Getter(AccessLevel.NONE)
    @StringLength
    private String firstPostId;
    @Getter(AccessLevel.NONE)
    @StringLength
    private String lastPostId;

    @Min(message = "Invalid page number", value = 1)
    private Long pageNumber;

    public enum SearchType {
        TITLE_ONLY, TITLE_OR_CONTENT, AUTHOR
    }

    public enum PagingType {
        PREV, NEXT
    }

    public void validate() {
        if (searchType != null && (query == null || query.length() < 2)) throw new IllegalArgumentException("검색어를 두 글자 이상 입력해주세요");
    }

    public String getBasePostId() {
        if (pagingType == null) throw new IllegalAccessError("pagingType is null. should not access postId.");
        return pagingType == PagingType.NEXT ? lastPostId : firstPostId;
    }

    public Long getPageNumber() {
        return pageNumber == null ? Long.valueOf(1) : pageNumber;
    }
}

@Documented
@Constraint(validatedBy = StringLengthValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@interface StringLength {
    String message() default "String length must be 24";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

class StringLengthValidator implements ConstraintValidator<StringLength, String> {
    @Override
    public void initialize(StringLength constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || ObjectId.isValid(value);
    }
}
