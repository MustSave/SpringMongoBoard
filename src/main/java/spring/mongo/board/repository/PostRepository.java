package spring.mongo.board.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import spring.mongo.board.entity.Post;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {

}
