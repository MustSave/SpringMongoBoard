package spring.mongo.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.mongo.board.dto.PostForm;
import spring.mongo.board.entity.Member;
import spring.mongo.board.entity.Post;
import spring.mongo.board.repository.MemberRepository;
import spring.mongo.board.repository.PostRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public void save(PostForm form, String userId) {
        Optional<Member> member = memberRepository.findById(userId);
        if (member.isEmpty()) throw new NoSuchElementException();

        Post post = new Post(member.get(), form.getTitle(), form.getContent());
        postRepository.save(post);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(String postId) {
        return postRepository.findById(postId);
    }

    public boolean saveComment(List<Integer> replies, String comment, String memberId, String postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) return false;

        List<Post.Comment> targetList = post.get().getComments();
        if (replies != null && !replies.isEmpty()) {
            for (Integer replyIdx : replies) {
                if (targetList.size() <= replyIdx) return false;
                targetList = targetList.get(replyIdx).getReplies();
            }
        }

        targetList.add(new Post.Comment(new Member(memberId), comment));
        postRepository.save(post.get());
        return true;
    }

    public boolean deleteById(String postId, String memberId) {
        Optional<Post> post = postRepository.findPostByIdAndWriterId(postId, memberId);
        if (post.isEmpty()) return false;

        postRepository.deleteById(postId);
        return true;
    }
}
