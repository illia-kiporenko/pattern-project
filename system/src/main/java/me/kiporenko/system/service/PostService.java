package me.kiporenko.system.service;

import me.kiporenko.system.model.Post;
import me.kiporenko.system.model.dto.PageDTO; // <-- Import PageDTO
import me.kiporenko.system.model.dto.PostResponseDTO;
import org.springframework.data.domain.Pageable;

public interface PostService {
    // Update the return type
    PageDTO<PostResponseDTO> getPosts(Long blogId, Pageable pageable);

    Post getPostById(Long id);
    Post createPost(Post post);
    void deletePost(Long postId);
    void updatePost(Post post, Long id);
}