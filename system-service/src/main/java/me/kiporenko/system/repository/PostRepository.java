package me.kiporenko.system.repository;

import me.kiporenko.system.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // The method now accepts a Pageable object and returns a Page of Posts
    Page<Post> findAllByBlogId(Long blogId, Pageable pageable);

    void deletePostById(Long postId);
}