package ksh.backendserver.post.model;

import ksh.backendserver.company.entity.Company;
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
public class PostSkillRequirement {

    private Post post;
    private Company company;
    private List<PostSkillWithSkill> skills;

    public static PostSkillRequirement of(PostWithCompany postWithCompany, List<PostSkillWithSkill> skills) {
        Post post = postWithCompany.getPost();
        Company company = postWithCompany.getCompany();

        return new PostSkillRequirement(post, company, skills);
    }

    public boolean matchesWith(UserSubscription userSubscription) {
        return skillMatchWith(userSubscription.getSkillIds())
            && industryMatchWith(userSubscription.getIndustryIds())
            && companyMatchWith(userSubscription.getCompanyIds());
    }

    private boolean skillMatchWith(Set<Long> skillSet) {
        if (skillSet.isEmpty()) return true;

        return skills.stream()
            .map(PostSkillWithSkill::getPostSkill)
            .map(PostSkill::getSkillId)
            .anyMatch(skillSet::contains);
    }

    private boolean industryMatchWith(Set<Long> positionSet) {
        if (positionSet.isEmpty()) return true;

        return positionSet.contains(post.getIndustryId());
    }

    private boolean companyMatchWith(Set<Long> companySet) {
        if (companySet.isEmpty()) return true;

        return companySet.contains(post.getCompanyId());
    }
}
