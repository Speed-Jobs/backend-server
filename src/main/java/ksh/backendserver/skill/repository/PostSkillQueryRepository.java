package ksh.backendserver.skill.repository;

import ksh.backendserver.company.enums.DateRange;
import ksh.backendserver.skill.dto.projection.SkillWithCount;

import java.time.LocalDate;
import java.util.List;

public interface PostSkillQueryRepository {

    List<String> findSkillNamesByPostId(Long postId);

    List<SkillWithCount> findTopSkillOrderByCountDesc(int size, DateRange timePeriod, LocalDate end);
}
