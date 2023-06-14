package spring.mongo.board.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import spring.mongo.board.entity.Session;

public interface SessionRepository extends MongoRepository<Session, String> {

}
