package ksh.backendserver.post.repository;

import ksh.backendserver.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long>, PostQueryRepository {
}
