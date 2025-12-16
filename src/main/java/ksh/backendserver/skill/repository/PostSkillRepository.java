package ksh.backendserver.skill.repository;

import ksh.backendserver.skill.entity.PostSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostSkillRepository extends JpaRepository<PostSkill, Long>, PostSkillQueryRepository {

    List<PostSkill> findByPostIdIn(List<Long> postIds);

    List<PostSkill> findByPostId(long postId);
}
