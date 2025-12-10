package ksh.backendserver.skill.service;

import ksh.backendserver.post.entity.Post;
import ksh.backendserver.post.model.PostSkillRequirement;
import ksh.backendserver.skill.entity.PostSkill;
import ksh.backendserver.skill.repository.PostSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostSkillService {

    private final PostSkillRepository postSkillRepository;

    @Transactional(readOnly = true)
    public List<PostSkillRequirement> findSkillRequirementsOf(List<Post> posts) {
        List<Long> ids = posts.stream()
            .map(Post::getId)
            .toList();

        List<PostSkill> postSkills = postSkillRepository.findByPostIdIn(ids);

        Map<Long, List<PostSkill>> postSkillMap = postSkills.stream()
            .collect(Collectors.groupingBy(PostSkill::getPostId));

        return posts.stream()
            .filter(post -> postSkillMap.containsKey(post.getId()))
            .map(post -> PostSkillRequirement.of(post, postSkillMap.get(post.getId())))
            .toList();
    }
}
