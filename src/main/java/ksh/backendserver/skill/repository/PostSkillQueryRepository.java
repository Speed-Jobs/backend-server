package ksh.backendserver.skill.repository;

import ksh.backendserver.company.enums.DateRange;
import ksh.backendserver.skill.dto.projection.SkillWithCount;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PostSkillQueryRepository {

    List<String> findSkillNamesByPostId(Long postId);

    List<SkillWithCount> findTopSkillOrderByCountDesc(int size, DateRange timePeriod, LocalDate end);

    Long countBySkillIdSince(long skillId, LocalDateTime baseTime);

    Long countBySkillIdBetween(long skillId, LocalDateTime start, LocalDateTime endExclusive);
}
