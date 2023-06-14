package spring.mongo.board.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import spring.mongo.board.entity.Member;

public interface MemberRepository extends MongoRepository<Member, String> {
}
