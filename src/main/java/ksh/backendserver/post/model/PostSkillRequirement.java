package ksh.backendserver.post.model;

import ksh.backendserver.post.entity.Post;
import ksh.backendserver.skill.entity.PostSkill;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostSkillRequirement {

    private Post post;
    private List<PostSkill> skills;

    public static PostSkillRequirement of(Post post, List<PostSkill> skills) {
        return new PostSkillRequirement(post, skills);
    }
}
