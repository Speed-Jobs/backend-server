package ksh.backendserver.skill.service;

import ksh.backendserver.post.dto.projection.PostWithCompany;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.post.model.PostSkillRequirement;
import ksh.backendserver.role.entity.Industry;
import ksh.backendserver.role.repository.IndustryRepository;
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
    private final IndustryRepository industryRepository;

    @Transactional(readOnly = true)
    public List<PostSkillRequirement> findSkillRequirementsOf(List<PostWithCompany> posts) {
        List<Long> postIds = posts.stream()
            .map(PostWithCompany::getPost)
            .map(Post::getId)
            .toList();

        List<PostSkillWithSkill> postSkills = postSkillRepository.findWithSkillByPostIdIn(postIds);

        Map<Long, List<PostSkillWithSkill>> postSkillMap = postSkills.stream()
            .collect(Collectors.groupingBy(ps -> ps.getPostSkill().getPostId()));

        List<Long> industryIds = posts.stream()
            .map(PostWithCompany::getPost)
            .map(Post::getIndustryId)
            .distinct()
            .toList();

        Map<Long, Industry> industryMap = industryRepository.findByIdIn(industryIds).stream()
            .collect(Collectors.toMap(Industry::getId, industry -> industry));

        return posts.stream()
            .filter(post -> postSkillMap.containsKey(post.getPost().getId()))
            .map(post -> {
                Industry industry = industryMap.get(post.getPost().getIndustryId());
                return PostSkillRequirement.of(post, industry, postSkillMap.get(post.getPost().getId()));
            })
            .toList();
    }
}
