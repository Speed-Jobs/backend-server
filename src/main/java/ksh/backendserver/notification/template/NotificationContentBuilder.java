package ksh.backendserver.notification.template;

import ksh.backendserver.post.model.PostSkillRequirement;
import ksh.backendserver.skill.dto.projection.PostSkillWithSkill;
import ksh.backendserver.skill.entity.Skill;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationContentBuilder {

    private static final int MAX_SKILL_DISPLAY_COUNT = 5;
    private static final String NO_SKILL_INFO = "정보 없음";

    public String buildSummary(List<PostSkillRequirement> posts) {
        return String.format("[SpeedJobs] 새로운 매칭 공고 %d건이 도착했습니다.", posts.size());
    }

    public String buildBody(List<PostSkillRequirement> posts) {
        StringBuilder sb = new StringBuilder();

        sb.append("안녕하세요.\n\n")
            .append("설정하신 조건에 맞는 신규 채용 공고가 도착했습니다.\n")
            .append("아래 공고를 확인해보세요 👇\n\n");

        for (PostSkillRequirement requirement : posts) {
            appendPostInfo(sb, requirement);
        }

        sb.append("서비스에서 전체 공고를 확인할 수 있습니다.\n")
            .append("SpeedJobs 드림.");

        return sb.toString();
    }

    private void appendPostInfo(StringBuilder sb, PostSkillRequirement requirement) {
        String company = requirement.getCompany().getName();
        String title = requirement.getPost().getTitle();
        String skills = extractSkillNames(requirement.getSkills());

        sb.append(String.format("%s - %s\n", company, title))
            .append(String.format("필요 기술: %s\n\n", skills));
    }

    private String extractSkillNames(List<PostSkillWithSkill> skills) {
        if (skills == null || skills.isEmpty()) {
            return NO_SKILL_INFO;
        }

        return skills.stream()
            .map(PostSkillWithSkill::getSkill)
            .map(Skill::getName)
            .limit(MAX_SKILL_DISPLAY_COUNT)
            .collect(Collectors.joining(", "));
    }
}
