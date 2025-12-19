package ksh.backendserver.post.repository;

import ksh.backendserver.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostQueryRepository {

    long countByPostedAtGreaterThanEqual(LocalDateTime baseTime);

    List<Post> findByCrawledAtAfter(LocalDateTime baseTime);
}
