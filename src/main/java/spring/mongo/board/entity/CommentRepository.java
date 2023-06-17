package spring.mongo.board.entity;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<PostComment, String> {

}
