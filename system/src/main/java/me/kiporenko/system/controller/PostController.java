package me.kiporenko.system.controller;

import me.kiporenko.system.model.Post;
import me.kiporenko.system.model.dto.PageDTO;
import me.kiporenko.system.model.dto.PostResponseDTO;
import me.kiporenko.system.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "api/post")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    // Update the return type
    public ResponseEntity<PageDTO<PostResponseDTO>> getAllPostsByBlogId(@RequestParam Long blogId, Pageable pageable) {
        PageDTO<PostResponseDTO> postPage = postService.getPosts(blogId, pageable);
        return ResponseEntity.ok(postPage);
    }

    @GetMapping("/{postId}")
    public Post getPostById(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }

    @PostMapping
    public ResponseEntity<Post> addPost(@RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@RequestBody Post post, @PathVariable Long postId) {
        postService.updatePost(post, postId);
        Post updatedPost = postService.getPostById(postId);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePostById(@PathVariable Long postId) {
        postService.deletePost(postId);
    }
}