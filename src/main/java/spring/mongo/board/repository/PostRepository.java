package spring.mongo.board.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import spring.mongo.board.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends MongoRepository<Post, String> {
    public Optional<Post> findPostByIdAndWriterId(String postId, String writerId);
}
