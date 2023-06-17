package spring.mongo.board.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import spring.mongo.board.entity.Post;
import spring.mongo.board.entity.PostComment;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final MongoTemplate mongoTemplate;

    public Post save(Post post) {
        if (post.getComments() == null) {
            PostComment postComment = new PostComment();
            mongoTemplate.save(postComment);
            post.setComments(postComment);
        }
        mongoTemplate.save(post);
        return post;
    }

    public void delete(String postId) {
        Query query = new Query(Criteria.where("_id").is(postId));
        Post postRemoved = mongoTemplate.findAndRemove(query, Post.class);

        query = new Query(Criteria.where("_id").is(postRemoved.getComments().getId()));
        mongoTemplate.remove(query, PostComment.class);
    }

    public Optional<Post> findById(String postId) {
        Query query = new Query(Criteria.where("_id").is(postId));
        return Optional.ofNullable(mongoTemplate.findById(postId, Post.class));
    }

    public Optional<Post> findPostByIdAndWriterId(String postId, String memberId) {
        Query query = new Query(Criteria.where("_id").is(postId).and("writer.id").is(memberId));
        Post post = mongoTemplate.findOne(query, Post.class);
        return Optional.ofNullable(post);
    }

    public List<Post> findAllWithoutComment() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project().andExclude("comments")
        );

        return mongoTemplate
                .aggregate(aggregation, mongoTemplate.getCollectionName(Post.class), Post.class)
                .getMappedResults();
    }

    public List<Post> findAllWithoutComment(Aggregation aggregation) {
        List<Post> queryResults = mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(Post.class), Post.class)
                .getMappedResults();

        return queryResults;
    }
}