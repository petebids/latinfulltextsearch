package io.petebids.latinfulltextsearch.controller;


import io.petebids.latinfulltextsearch.controller.model.NewPostRequest;
import io.petebids.latinfulltextsearch.domain.model.Post;
import io.petebids.latinfulltextsearch.domain.service.impl.PostServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
public class PostController {

    @Autowired
    private PostServiceImpl postService;


    @PostMapping("/posts")
    public ResponseEntity<Post> create(@RequestBody NewPostRequest request) {
        log.info("received {}", request);
        String content = request.getContent();
        String title = request.getTitle();
        if (content == null || content.equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content must not be null!");
        }
        Post post = postService.createPost(content, title);

        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @GetMapping("/posts/{id}/")
    public ResponseEntity<Post> getPostByID(@PathVariable String id) {
        log.info("get invoked with ", id);
        Post post = postService.getById(id);
        log.info("post found {}", post);
        if (post == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(post);
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<Post>> getPageByTerm(@RequestParam String content, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "0") int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Post> posts = postService.getPageByPartialContent(content, pageable);
        return ResponseEntity.ok(posts);


    }


}
