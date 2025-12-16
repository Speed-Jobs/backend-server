package ksh.backendserver.post.model;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.post.dto.projection.PostWithCompany;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.role.entity.Industry;
import ksh.backendserver.skill.dto.projection.PostSkillWithSkill;
import ksh.backendserver.skill.entity.PostSkill;
import ksh.backendserver.subscription.model.UserSubscription;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public class PostSkillRequirement {

    private Post post;
    private Company company;
    private Industry industry;
    private List<PostSkillWithSkill> skills;

    public static PostSkillRequirement of(
        PostWithCompany postWithCompany,
        Industry industry,
        List<PostSkillWithSkill> skills
    ) {
        Post post = postWithCompany.getPost();
        Company company = postWithCompany.getCompany();

        return new PostSkillRequirement(post, company, industry, skills);
    }

    public boolean matchesWith(UserSubscription userSubscription) {
        return skillMatchWith(userSubscription.getSkillIds())
            && positionMatchWith(userSubscription.getPositionIds())
            && companyMatchWith(userSubscription.getCompanyIds());
    }

    private boolean skillMatchWith(Set<Long> skillSet) {
        if (skillSet.isEmpty()) return true;

        return skills.stream()
            .map(PostSkillWithSkill::getPostSkill)
            .map(PostSkill::getSkillId)
            .anyMatch(skillSet::contains);
    }

    private boolean positionMatchWith(Set<Long> positionSet) {
        if (positionSet.isEmpty()) return true;

        if (industry == null) return false;

        return positionSet.contains(industry.getPositionId());
    }

    private boolean companyMatchWith(Set<Long> companySet) {
        if (companySet.isEmpty()) return true;

        return companySet.contains(post.getCompanyId());
    }
}
