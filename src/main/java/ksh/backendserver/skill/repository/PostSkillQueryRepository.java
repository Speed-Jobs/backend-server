package ksh.backendserver.skill.repository;

import java.util.List;

public interface PostSkillQueryRepository {

    List<String> findSkillNamesByPostId(Long postId);
}