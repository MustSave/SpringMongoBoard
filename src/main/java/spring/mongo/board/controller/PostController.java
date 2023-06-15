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

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

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

    @GetMapping("/posts")
    @ResponseBody
    public List<Post> getPost() {
        return postService.findAll();
    }
}
