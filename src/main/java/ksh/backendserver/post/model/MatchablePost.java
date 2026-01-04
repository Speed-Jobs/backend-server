package ksh.backendserver.post.model;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.jobrole.entity.JobRole;
import ksh.backendserver.post.dto.projection.PostWithCompany;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.skill.dto.projection.PostSkillWithSkill;
import ksh.backendserver.skill.entity.PostSkill;
import ksh.backendserver.subscription.model.UserSubscription;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public class MatchablePost {

    private Post post;
    private Company company;
    private JobRole jobRole;
    private List<PostSkillWithSkill> skills;

    public static MatchablePost of(
        PostWithCompany postWithCompany,
        JobRole jobRole,
        List<PostSkillWithSkill> skills
    ) {
        Post post = postWithCompany.getPost();
        Company company = postWithCompany.getCompany();

        return new MatchablePost(post, company, jobRole, skills);
    }

    public boolean matchesWith(UserSubscription userSubscription) {
        return matchesSkill(userSubscription.getSkillIds())
            && matchesJobField(userSubscription.getJobFieldIds())
            && matchesCompany(userSubscription.getCompanyIds());
    }

    private boolean matchesSkill(Set<Long> userSkillIds) {
        if (userSkillIds.isEmpty()) return true;

        return skills.stream()
            .map(PostSkillWithSkill::getPostSkill)
            .map(PostSkill::getSkillId)
            .anyMatch(userSkillIds::contains);
    }

    private boolean matchesJobField(Set<Long> userJobFieldIds) {
        if (userJobFieldIds.isEmpty()) return true;

        if (jobRole == null) return false;

        return userJobFieldIds.contains(jobRole.getJobFieldId());
    }

    private boolean matchesCompany(Set<Long> userCompanyIds) {
        if (userCompanyIds.isEmpty()) return true;

        return userCompanyIds.contains(post.getCompanyId());
    }
}