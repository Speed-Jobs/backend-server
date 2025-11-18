package ksh.backendserver.post.repository;

import ksh.backendserver.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PostRepository extends JpaRepository<Post,Long>, PostQueryRepository {

    long countByPostedAtGreaterThanEqual(LocalDateTime baseTime);
}
