package me.kiporenko.system.repository;

import me.kiporenko.system.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    Blog getBlogById(Long id);

    void deleteBlogById(Long id);
}