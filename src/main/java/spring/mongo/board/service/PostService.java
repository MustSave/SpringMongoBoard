package spring.mongo.board.service;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import spring.mongo.board.dto.PostForm;
import spring.mongo.board.dto.PostSearchDTO;
import spring.mongo.board.entity.Comment;
import spring.mongo.board.entity.CommentRepository;
import spring.mongo.board.entity.Member;
import spring.mongo.board.entity.Post;
import spring.mongo.board.repository.MemberRepository;
import spring.mongo.board.repository.PostRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    public void save(PostForm form, Member member) {
        Post post = new Post(member, form.getTitle(), form.getContent());
        postRepository.save(post);
    }

    public Optional<Post> findById(String postId) {
        return postRepository.findById(postId);
    }

    public boolean saveComment(List<Integer> replies, String comment, Member member, String postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) return false;

        List<Comment> targetList = post.get().getComments().getComments();
        if (replies != null && !replies.isEmpty()) {
            for (Integer replyIdx : replies) {
                if (targetList.size() <= replyIdx) return false;
                targetList = targetList.get(replyIdx).getReplies();
            }
        }

        targetList.add(new Comment(member, comment));
        commentRepository.save(post.get().getComments());
        return true;
    }

    public boolean update(String postId, String memberId, PostForm form) {
        Optional<Post> post = postRepository.findPostByIdAndWriterId(postId, memberId);
        if (post.isEmpty()) return false;

        post.get().update(form.getTitle(), form.getContent());
        postRepository.save(post.get());
        return true;
    }

    public boolean deleteById(String postId, String memberId) {
        Optional<Post> post = postRepository.findPostByIdAndWriterId(postId, memberId);
        if (post.isEmpty()) return false;

        postRepository.delete(postId);
        return true;
    }

    public int deleteComment(String postId, int[] commentIndexes, String memberId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) return HttpStatus.BAD_REQUEST.value();

        Comment comment = null;
        List<Comment> targetList = post.get().getComments().getComments();

        for (int commentIndex : commentIndexes) {
            if (commentIndex < 0 || targetList.size() <= commentIndex) return HttpStatus.BAD_REQUEST.value();

            comment = targetList.get(commentIndex);
            targetList = targetList.get(commentIndex).getReplies();
        }

        // 댓글 작성자인지 확인
        if ((comment == null) || (comment.getWriter() == null) || !comment.getWriter().getId().equals(memberId)) return HttpStatus.UNAUTHORIZED.value();

        comment.delete();
        commentRepository.save(post.get().getComments());
        return 200;
    }

    public int updateComment(String postId, int[] commentIndexes, String memberId, String content) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) return HttpStatus.BAD_REQUEST.value();

        Comment comment = null;
        List<Comment> targetList = post.get().getComments().getComments();

        for (int commentIndex : commentIndexes) {
            if (commentIndex < 0 || targetList.size() <= commentIndex) return HttpStatus.BAD_REQUEST.value();

            comment = targetList.get(commentIndex);
            targetList = targetList.get(commentIndex).getReplies();
        }

        // 댓글 작성자인지 확인
        if ((comment == null) || (comment.getWriter() == null) || !comment.getWriter().getId().equals(memberId)) return HttpStatus.UNAUTHORIZED.value();

        comment.update(content);
        commentRepository.save(post.get().getComments());
        return 200;
    }

    public List<Post> findAllWithQuery(PostSearchDTO postSearchDTO, long postPerPage) {
        PostSearchDTO.SearchType searchType = postSearchDTO.getSearchType();
        Criteria criteria = new Criteria();
        Aggregation aggregation;

        if (searchType != null) {
            Criteria expr = Criteria.expr(() -> new Document("$gt", List.of("$_id", new Document("$toObjectId", postSearchDTO.getBasePostId()))));
            switch (searchType) {
                case TITLE_OR_CONTENT, TITLE_ONLY -> {
                    criteria = criteria.andOperator(expr);
                    Criteria titleCriteria = Criteria.expr(()->new Document("$regexMatch", new Document(Map.of("input", "$title", "regex", postSearchDTO.getQuery(), "options", "i"))));
                    Criteria contentCriteria = Criteria.expr(()->new Document("$regexMatch", new Document(Map.of("input", "$content", "regex", postSearchDTO.getQuery(), "options", "i"))));

                    criteria = searchType.equals(PostSearchDTO.SearchType.TITLE_ONLY) ? criteria.orOperator(titleCriteria) : criteria.orOperator(titleCriteria, contentCriteria);
                    aggregation = Aggregation.newAggregation(Aggregation.match(criteria));
                }
                case AUTHOR -> aggregation = Aggregation.newAggregation(LookupOperation.newLookup()
                                .from("members")
                                .localField("writer.$id")
                                .foreignField("_id")
                                .as("writer"),
                        Aggregation.match(criteria.andOperator(expr, Criteria.where("writer.0.nickname").is(postSearchDTO.getQuery()))));
                default -> {
                    return null;
                }
            }
        } else {
            aggregation = Aggregation.newAggregation(Aggregation.match(l->null));
        }

        // 페이징 && 댓글은 조회에서 제외
        aggregation.getPipeline()
            .add(Aggregation.sort(Sort.Direction.DESC, "_id"))
            .add(Aggregation.limit(postPerPage))
            .add(Aggregation.project().andExclude("comments"));
        return postRepository.findAllWithoutComment(aggregation);
    }
}
