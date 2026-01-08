package ksh.backendserver.post.service;

import ksh.backendserver.jobrole.entity.JobRole;
import ksh.backendserver.jobrole.repository.JobRoleRepository;
import ksh.backendserver.post.dto.projection.PostWithCompany;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.post.model.MatchablePost;
import ksh.backendserver.post.repository.PostRepository;
import ksh.backendserver.skill.dto.projection.PostSkillWithSkill;
import ksh.backendserver.skill.repository.PostSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchablePostService {

    private final PostRepository postRepository;
    private final PostSkillRepository postSkillRepository;
    private final JobRoleRepository jobRoleRepository;

    @Transactional(readOnly = true)
    public List<MatchablePost> findNewMatchablePostsAfter(LocalDateTime checkpoint) {
        List<PostWithCompany> posts = postRepository.findByCrawledAtAfterCheckpoint(checkpoint);

        if (posts.isEmpty()) {
            return List.of();
        }

        return buildMatchablePosts(posts);
    }

    @Transactional(readOnly = true)
    public MatchablePost findMatchablePostById(Long postId) {
        var postWithCompanyAndRole = postRepository.getByIdWithCompanyAndRole(postId);
        List<PostSkillWithSkill> skills = postSkillRepository.findWithSkillByPostIdIn(List.of(postId));

        var postWithCompany = new PostWithCompany(
            postWithCompanyAndRole.getPost(),
            postWithCompanyAndRole.getCompany()
        );

        return MatchablePost.of(postWithCompany, postWithCompanyAndRole.getJobRole(), skills);
    }

    private List<MatchablePost> buildMatchablePosts(List<PostWithCompany> posts) {
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
                List<PostSkillWithSkill> usedSkills = postSkillMap.get(post.getPost().getId());
                return MatchablePost.of(post, jobRole, usedSkills);
            })
            .toList();
    }
}
