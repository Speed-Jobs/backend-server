package ksh.backendserver.skill.service;

import ksh.backendserver.post.dto.projection.PostWithCompany;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.post.model.PostSkillRequirement;
import ksh.backendserver.skill.dto.projection.PostSkillWithSkill;
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
    public List<PostSkillRequirement> findSkillRequirementsOf(List<PostWithCompany> posts) {
        List<Long> ids = posts.stream()
            .map(PostWithCompany::getPost)
            .map(Post::getId)
            .toList();

        List<PostSkillWithSkill> postSkills = postSkillRepository.findWithSkillByPostIdIn(ids);

        Map<Long, List<PostSkillWithSkill>> postSkillMap = postSkills.stream()
            .collect(Collectors.groupingBy(ps -> ps.getPostSkill().getPostId()));

        return posts.stream()
            .filter(post -> postSkillMap.containsKey(post.getPost().getId()))
            .map(post -> PostSkillRequirement.of(post, postSkillMap.get(post.getPost().getId())))
            .toList();
    }
}
