package spring.mongo.board.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import spring.mongo.board.entity.Session;

import java.util.Optional;

public interface SessionRepository extends MongoRepository<Session, String> {
    public void deleteBySessionId(String sessionId);
}
