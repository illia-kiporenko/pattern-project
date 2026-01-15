package me.kiporenko.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.kiporenko.system.exception.EntityNotFoundException;
import me.kiporenko.system.mapper.PostMapper;
import me.kiporenko.system.model.Post;
import me.kiporenko.common.dto.PageDTO;
import me.kiporenko.system.model.dto.PostResponseDTO;
import me.kiporenko.system.repository.PostRepository;
import me.kiporenko.system.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    @Override
    @Cacheable(cacheNames = "posts_by_blog", key = "#blogId + '-page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize + '-sort-' + #pageable.sort.hashCode()")
    public PageDTO<PostResponseDTO> getPosts(Long blogId, Pageable pageable) {
        log.info("DATABASE HIT: Fetching posts for blog Id: {} and page: {}", blogId, pageable.getPageNumber());
        Page<Post> postPage = postRepository.findAllByBlogId(blogId, pageable);

        Page<PostResponseDTO> dtoPage = postPage.map(postMapper::toDto);

        return new PageDTO<>(dtoPage);
    }

    @Override
    @Cacheable(cacheNames = "posts", key = "#id")
    public Post getPostById(Long id) {
        log.info("DATABASE HIT: Fetching post by id: {}", id);
        return postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post", id));
    }

    @Override
    @Transactional
    // FIX: Explicitly name the cache
    @CacheEvict(cacheNames = "posts_by_blog", allEntries = true)
    public Post createPost(Post post) {
        log.info("Creating post: {} and invalidating 'posts_by_blog' cache", post);
        return postRepository.save(post);
    }

    @Override
    @Transactional
    // FIX: Explicitly name the cache in both inner annotations
    @Caching(evict = {
            @CacheEvict(cacheNames = "posts", key = "#id"),
            @CacheEvict(cacheNames = "posts_by_blog", allEntries = true)
    })
    public void updatePost(Post post, Long id) {
        log.info("Updating post with id {}", id);
        Post postToUpdate = this.getPostById(id);

        if (post.getTitle() != null) postToUpdate.setTitle(post.getTitle());
        if (post.getPlot() != null) postToUpdate.setPlot(post.getPlot());

        postRepository.save(postToUpdate);
    }

    @Override
    @Transactional
    // FIX: Explicitly name the cache in both inner annotations
    @Caching(evict = {
            @CacheEvict(cacheNames = "posts", key = "#postId"),
            @CacheEvict(cacheNames = "posts_by_blog", allEntries = true)
    })
    public void deletePost(Long postId) {
        log.info("Deleting post with id {}", postId);
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post", postId);
        }
        postRepository.deleteById(postId);
    }

    private PostResponseDTO convertToDto(Post post) {
        return new PostResponseDTO(post.getId(), post.getTitle(), post.getPlot(), post.getLikesNumber(), post.getDislikesNumber());
    }
}