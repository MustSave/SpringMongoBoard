package spring.mongo.board.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import spring.mongo.board.entity.Member;
import spring.mongo.board.entity.Session;

import java.util.Optional;

public interface SessionRepository extends MongoRepository<Session, String> {
    public void deleteBySessionId(String sessionId);
    public Optional<Session> findBySessionId(String sessionId);

    @Aggregation(pipeline = {
            "{$match: {sessionId: {$eq: '?0'}}}",
            "{$lookup: {from: 'members', localField: 'memberId', foreignField: '_id', as: 'member'}}",
            "{$project: {member: {$arrayElemAt: ['$member', 0]}, _id: 0}}",
            "{$replaceRoot: {newRoot: '$member'}}"
    })
    public Optional<Member> findMemberBySessionId(String sessionId);
}
