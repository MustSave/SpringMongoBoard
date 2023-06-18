package spring.mongo.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
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
import java.util.NoSuchElementException;
import java.util.Optional;

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

    public List<Post> findAll() {
        return postRepository.findAllWithoutComment();
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

    public List<Post> findAllWithQuery(PostSearchDTO postSearchDTO) {
        PostSearchDTO.SearchType searchType = postSearchDTO.getSearchTypeEnum();
        if (searchType == null) {
            return findAll();
        }

        Aggregation aggregation;
        switch (searchType) {
            case TITLE_ONLY:
                aggregation = Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("title").regex(postSearchDTO.getQuery()))
                );
                break;
            case TITLE_OR_CONTENT:
                aggregation = Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("title").regex(postSearchDTO.getQuery()).orOperator(Criteria.where("content").regex("title")))
                );
                break;
            case AUTHOR:
                aggregation = Aggregation.newAggregation(
                        LookupOperation.newLookup()
                            .from("members")
                            .localField("writer.$id")
                            .foreignField("_id")
                            .as("writer"),
                        Aggregation.match(Criteria.where("writer.0.nickname").is(postSearchDTO.getQuery()))
                );
                break;
            default:
                return null;
        }

        // 댓글은 조회에서 제외
        aggregation.getPipeline().add(Aggregation.project().andExclude("comments"));
        return postRepository.findAllWithoutComment(aggregation);
    }
}
