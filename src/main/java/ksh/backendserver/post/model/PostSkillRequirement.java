package ksh.backendserver.post.model;

import ksh.backendserver.post.entity.Post;
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
    private List<PostSkill> skills;

    public static PostSkillRequirement of(Post post, List<PostSkill> skills) {
        return new PostSkillRequirement(post, skills);
    }

    public boolean matchesWith(UserSubscription userSubscription) {
        return skillMatchWith(userSubscription.getSkillIds())
            && industryMatchWith(userSubscription.getIndustryIds())
            && companyMatchWith(userSubscription.getCompanyIds());
    }

    private boolean skillMatchWith(Set<Long> skillSet) {
        if (skillSet.isEmpty()) return true;

        return skills.stream()
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
