package me.kiporenko.system.service;

import me.kiporenko.system.model.Blog;
import me.kiporenko.system.model.dto.BlogResponseDTO;
import me.kiporenko.common.dto.PageDTO; // <-- Import PageDTO
import org.springframework.data.domain.Pageable;

public interface BlogService {
    // Update the return type here
    PageDTO<BlogResponseDTO> getBlogs(Pageable pageable);

    Blog getBlogById(Long id);
    Blog createBlog(Blog blog);
    void deleteBlog(Long id);
    Blog updateBlog(Blog blog, long id);
}