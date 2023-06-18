package spring.mongo.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spring.mongo.board.dto.PostForm;
import spring.mongo.board.dto.PostSearchDTO;
import spring.mongo.board.dto.ResponseMessage;
import spring.mongo.board.entity.Member;
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
    public ResponseEntity getAllPost(@ModelAttribute @Validated PostSearchDTO postSearchDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(bindingResult.getAllErrors().get(0).getDefaultMessage()));
        }
        try {
            postSearchDTO.validate();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        }
        return ResponseEntity.status(200).body(postService.findAllWithQuery(postSearchDTO, 10l));
    }

    @PostMapping("/posts")
    public ResponseEntity<ResponseMessage> createPost(PostForm form, @RequestAttribute("member") Member member) {
        HttpStatus statusCode;
        String responseBody;

        try {
            postService.save(form, member);
            statusCode = HttpStatus.OK;
            responseBody = "OK";
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            responseBody = "게시글 등록 실패";
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
                                                      @RequestAttribute("member") Member member,
                                                      PostForm form) {
        HttpStatus statusCode;
        String responseBody;

        boolean updated = postService.update(postId, member.getId(), form);

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
                                                      @RequestAttribute("member") Member member) {
        boolean deleted = postService.deleteById(postId, member.getId());
        return ResponseEntity
                .status(deleted ? HttpStatus.OK : HttpStatus.UNAUTHORIZED)
                .body(new ResponseMessage(deleted ? "OK" : "권한이 없습니다."));
    }

    @PostMapping("/posts/{id}")
    @ResponseBody
    public ResponseEntity<ResponseMessage> createComment(
                                        @RequestParam(name = "replies", required = false) List<Integer> replies,
                                        @RequestParam("comment") String comment,
                                        @RequestAttribute("member") Member member,
                                        @PathVariable("id") String postId) {
        boolean success = postService.saveComment(replies, comment, member, postId);
        return ResponseEntity
                .status(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage(success ? "OK" : "Invalid Parameter"));
    }

    @PatchMapping("/posts/{id}/{commentIdxStr}")
    public ResponseEntity<ResponseMessage> updateComment(@PathVariable("id") String postId,
                                                         @PathVariable("commentIdxStr") String commentIdxStr,
                                                         @RequestAttribute("member") Member member,
                                                         @RequestParam("comment") String comment) {
        int[] commentIndexes;
        try {
            commentIndexes = parseStringAndConvertToInt(commentIdxStr, ",");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Not an integer"));
        }

        int statusCode = postService.updateComment(postId, commentIndexes, member.getId(), comment);
        return ResponseEntity.status(statusCode).body(new ResponseMessage(statusCode==200?"Updated":"Error"));
    }

    @DeleteMapping("/posts/{id}/{commentIdxStr}")
    public ResponseEntity<ResponseMessage> deleteComment(@PathVariable("id") String postId,
                                                         @PathVariable("commentIdxStr") String commentIdxStr,
                                                         @RequestAttribute("member") Member member) {
        int[] commentIndexes;
        int statusCode = 400;

        try {
            commentIndexes = parseStringAndConvertToInt(commentIdxStr, ",");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Not an integer"));
        }

        if (commentIndexes.length == 0 || (statusCode = postService.deleteComment(postId, commentIndexes, member.getId())) >= 300) {
            return ResponseEntity.status(statusCode).body(new ResponseMessage("Invalid Parm"));
        }

        return ResponseEntity.status(statusCode).body(new ResponseMessage("Deleted"));
    }

    private int[] parseStringAndConvertToInt(String text, String parseRegex) {
        return Arrays.stream(text.split(parseRegex)).mapToInt(Integer::parseInt).toArray();
    }
}