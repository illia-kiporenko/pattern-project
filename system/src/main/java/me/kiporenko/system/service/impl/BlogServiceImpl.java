package me.kiporenko.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.kiporenko.system.exception.EntityNotFoundException;
import me.kiporenko.system.mapper.BlogMapper;
import me.kiporenko.system.model.Blog;
import me.kiporenko.system.model.dto.BlogResponseDTO;
import me.kiporenko.system.model.dto.PageDTO; // <-- Import PageDTO
import me.kiporenko.system.repository.BlogRepository;
import me.kiporenko.system.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final BlogMapper blogMapper;

    @Autowired
    public BlogServiceImpl(BlogRepository blogRepository, BlogMapper blogMapper) {
        this.blogRepository = blogRepository;
        this.blogMapper = blogMapper;
    }

    @Override
    @Cacheable(cacheNames = "blogs", key = "'page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize + '-sort-' + #pageable.sort.hashCode()")
    public PageDTO<BlogResponseDTO> getBlogs(Pageable pageable) {
        log.info("DATABASE HIT: Fetching blogs for page: {}", pageable.getPageNumber());
        Page<Blog> blogPage = blogRepository.findAll(pageable);
        Page<BlogResponseDTO> dtoPage = blogPage.map(blogMapper::toDto);

        return new PageDTO<>(dtoPage);
    }

    // --- No changes needed for the rest of the methods below ---
    @Override
    @Cacheable(cacheNames = "blogs", key = "#id")
    public Blog getBlogById(Long id) {
        log.info("DATABASE HIT: Fetching blog with id: {}", id);
        return blogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog", id));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "blogs", allEntries = true)
    public Blog createBlog(Blog blog) {
        log.info("Creating blog {} and invalidating 'blogs' cache", blog);
        return blogRepository.save(blog);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "blogs", key = "#id"),
            @CacheEvict(cacheNames = "blogs", allEntries = true)
    })
    public Blog updateBlog(Blog blog, long id) {
        log.info("Updating blog with id {} and invalidating caches", id);
        Blog blogToUpdate = this.getBlogById(id);
        if (blog.getTitle() != null) {
            blogToUpdate.setTitle(blog.getTitle());
        }
        return blogRepository.save(blogToUpdate);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "blogs", key = "#id"),
            @CacheEvict(cacheNames = "blogs", allEntries = true)
    })
    public void deleteBlog(Long id) {
        log.info("Deleting blog with id {} and invalidating caches", id);
        if (!blogRepository.existsById(id)) {
            throw new EntityNotFoundException("Blog", id);
        }
        blogRepository.deleteById(id);
    }

    private BlogResponseDTO convertToDto(Blog blog) {
        return new BlogResponseDTO(blog.getId(), blog.getTitle(), blog.getAuthor());
    }
}