package me.kiporenko.system.controller;

import me.kiporenko.system.model.dto.BlogResponseDTO;
import me.kiporenko.common.dto.PageDTO;
import me.kiporenko.system.service.impl.BlogServiceImpl;
import me.kiporenko.system.service.impl.PostServiceImpl;
import me.kiporenko.system.model.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/blog")
public class BlogController {

    private final BlogServiceImpl blogService;

    private final PostServiceImpl postService;

    @Autowired
    public BlogController(BlogServiceImpl blogService, PostServiceImpl postService) {
        this.blogService = blogService;
        this.postService = postService;
    }

    @GetMapping
    // Update the return type
    public ResponseEntity<PageDTO<BlogResponseDTO>> getBlogs(Pageable pageable) {
        PageDTO<BlogResponseDTO> blogPage = blogService.getBlogs(pageable);
        return ResponseEntity.ok(blogPage);
    }

    // --- No changes needed for the rest of the methods below ---
    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getBlogById(id));
    }

    @PostMapping
    public ResponseEntity<Blog> addBlog(@RequestBody Blog blog) {
        Blog createdBlog = blogService.createBlog(blog);
        return new ResponseEntity<>(createdBlog, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Blog> updateBlog(@RequestBody Blog blog, @PathVariable Long id) {
        Blog updatedBlog = blogService.updateBlog(blog, id);
        return ResponseEntity.ok(updatedBlog);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
    }
}
