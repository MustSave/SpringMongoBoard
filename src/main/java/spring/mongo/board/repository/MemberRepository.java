package spring.mongo.board.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import spring.mongo.board.entity.Member;

import java.util.Optional;

public interface MemberRepository extends MongoRepository<Member, String> {
    public Optional<Member> findMemberByUsername(String username);
}
