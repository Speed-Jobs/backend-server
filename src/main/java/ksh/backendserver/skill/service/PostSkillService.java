package ksh.backendserver.skill.service;

import ksh.backendserver.post.dto.projection.PostWithCompany;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.post.model.PostSkillRequirement;
import ksh.backendserver.jobrole.entity.JobRole;
import ksh.backendserver.jobrole.repository.JobRoleRepository;
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
    private final JobRoleRepository jobRoleRepository;

    @Transactional(readOnly = true)
    public List<PostSkillRequirement> findSkillRequirementsOf(List<PostWithCompany> posts) {
        List<Long> postIds = posts.stream()
            .map(PostWithCompany::getPost)
            .map(Post::getId)
            .toList();

        List<PostSkillWithSkill> postSkills = postSkillRepository.findWithSkillByPostIdIn(postIds);

        Map<Long, List<PostSkillWithSkill>> postSkillMap = postSkills.stream()
            .collect(Collectors.groupingBy(ps -> ps.getPostSkill().getPostId()));

        List<Long> jobRoleIds = posts.stream()
            .map(PostWithCompany::getPost)
            .map(Post::getJobRoleId)
            .distinct()
            .toList();

        Map<Long, JobRole> jobRoleMap = jobRoleRepository.findByIdIn(jobRoleIds).stream()
            .collect(Collectors.toMap(JobRole::getId, jobRole -> jobRole));

        return posts.stream()
            .filter(post -> postSkillMap.containsKey(post.getPost().getId()))
            .map(post -> {
                JobRole jobRole = jobRoleMap.get(post.getPost().getJobRoleId());
                return PostSkillRequirement.of(post, jobRole, postSkillMap.get(post.getPost().getId()));
            })
            .toList();
    }
}
