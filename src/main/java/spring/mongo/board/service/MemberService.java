package spring.mongo.board.service;

import com.mongodb.MongoWriteException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring.mongo.board.entity.Member;
import spring.mongo.board.repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member join(Member member) {
        Member joinedMember = null;

        try {
            joinedMember = memberRepository.insert(member);
        } catch (DuplicateKeyException e) {
            return null;
        }
        return joinedMember;
    }
}
