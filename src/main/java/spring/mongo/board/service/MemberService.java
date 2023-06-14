package spring.mongo.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import spring.mongo.board.entity.Member;
import spring.mongo.board.repository.MemberRepository;

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
