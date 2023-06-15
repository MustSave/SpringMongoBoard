package spring.mongo.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import spring.mongo.board.dto.PostForm;
import spring.mongo.board.dto.ResponseMessage;
import spring.mongo.board.entity.Post;
import spring.mongo.board.service.PostService;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/posts")
    @ResponseBody
    public List<Post> getAllPost() {
        return postService.findAll();
    }

    @PostMapping("/posts")
    public ResponseEntity<ResponseMessage> createPost(PostForm form, @RequestAttribute("memberId") String memberId) {
        HttpStatus statusCode;
        String responseBody;

        try {
            postService.save(form, memberId);
            statusCode = HttpStatus.OK;
            responseBody = "OK";
        } catch (NoSuchElementException e) {
            statusCode = HttpStatus.UNAUTHORIZED;
            responseBody = "사용자 정보를 찾을 수 없음";
        }

        return ResponseEntity.status(statusCode).body(new ResponseMessage(responseBody));
    }

    @GetMapping("/posts/{id}")
    @ResponseBody
    public ResponseEntity getPostById(@PathVariable("id") String postId) {
        Optional<Post> post = postService.findById(postId);
        if (post.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("해당 게시글 ID가 존재하지 않습니다."));
        return ResponseEntity.status(200).body(post.get());
    }

    @PatchMapping("/posts/{id}")
    public ResponseEntity<ResponseMessage> updatePost(@PathVariable("id") String postId,
                                                      @RequestAttribute("memberId") String memberId,
                                                      PostForm form) {
        HttpStatus statusCode;
        String responseBody;

        boolean updated = postService.update(postId, memberId, form);

        if (updated) {
            statusCode = HttpStatus.OK;
            responseBody = "Updated";
        } else {
            statusCode = HttpStatus.UNAUTHORIZED;
            responseBody = "no permission or invalid content";
        }

        return ResponseEntity
                .status(statusCode)
                .body(new ResponseMessage(responseBody));
    }

    @DeleteMapping("/posts/{id}")
    @ResponseBody
    public ResponseEntity<ResponseMessage> deletePost(@PathVariable("id") String postId,
                                                      @RequestAttribute("memberId") String memberId) {
        boolean deleted = postService.deleteById(postId, memberId);
        return ResponseEntity
                .status(deleted ? HttpStatus.OK : HttpStatus.UNAUTHORIZED)
                .body(new ResponseMessage(deleted ? "OK" : "권한이 없습니다."));
    }

    @PostMapping("/posts/{id}")
    @ResponseBody
    public ResponseEntity<ResponseMessage> createComment(
                                        @RequestParam(name = "replies", required = false) List<Integer> replies,
                                        @RequestParam("comment") String comment,
                                        @RequestAttribute("memberId") String memberId,
                                        @PathVariable("id") String postId) {
        boolean success = postService.saveComment(replies, comment, memberId, postId);
        return ResponseEntity
                .status(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage(success ? "OK" : "Invalid Parameter"));
    }

    @DeleteMapping("/posts/{id}/{replies}")
    public ResponseEntity<ResponseMessage> deleteComment(@PathVariable("id") String postId,
                                                         @PathVariable("replies") String replies,
                                                         @RequestAttribute("memberId") String memberId) {
        int[] replyIds;
        try {
            replyIds = Arrays.stream(replies.split(",")).mapToInt(Integer::parseInt).toArray();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Not an integer"));
        }

        if (replyIds.length == 0 || !postService.deleteComment(postId, replyIds, memberId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid Parm"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Deleted"));
    }
}