package ksh.backendserver.skill.repository;

import ksh.backendserver.company.enums.DateRange;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.post.enums.EmploymentType;
import ksh.backendserver.post.enums.ExperienceLevel;
import ksh.backendserver.post.enums.PostStatus;
import ksh.backendserver.post.enums.WorkType;
import ksh.backendserver.post.repository.PostRepository;
import ksh.backendserver.skill.entity.PostSkill;
import ksh.backendserver.skill.entity.Skill;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
class PostSkillRepositoryTest {

    @Autowired
    private PostSkillRepository postSkillRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SkillRepository skillRepository;

    private Skill spring;
    private Skill fastApi;
    private Skill ktor;

    @BeforeEach
    void setUp() {
        spring = skillRepository.save(createSkill("Spring"));
        fastApi = skillRepository.save(createSkill("FastAPI"));
        ktor = skillRepository.save(createSkill("ktor"));

        Post springPost1 = createPost("스프링 공고 1", LocalDateTime.of(2025, 11, 1, 0, 0, 0), LocalDateTime.of(2025, 11, 16, 0, 0, 0), 1L, 1L);
        Post springPost2 = createPost("스프링 공고 2", LocalDateTime.of(2025, 11, 6, 0, 0, 0), LocalDateTime.of(2025, 11, 21, 0, 0, 0), 2L, 1L);
        Post springPost3 = createPost("스프링 공고 3", LocalDateTime.of(2025, 11, 7, 0, 0, 0), LocalDateTime.of(2025, 11, 22, 0, 0, 0), 3L, 1L);
        Post fastApiPost1 = createPost("FastAPI 공고 1", LocalDateTime.of(2025, 11, 2, 0, 0, 0), LocalDateTime.of(2025, 11, 17, 0, 0, 0), 4L, 2L);
        Post fastApiPost2 = createPost("FastAPI 공고 2", LocalDateTime.of(2025, 10, 25, 0, 0, 0), LocalDateTime.of(2025, 11, 10, 0, 0, 0), 5L, 2L);
        Post ktorPost1 = createPost("ktor 공고 1", LocalDateTime.of(2025, 10, 31, 0, 0, 0), LocalDateTime.of(2025, 11, 18, 0, 0, 0), 1L, 1L);
        postRepository.saveAll(List.of(springPost1, springPost2, springPost3, fastApiPost1, fastApiPost2, ktorPost1));

        PostSkill postSkill1 = createPostSkill(springPost1.getId(), spring.getId());
        PostSkill postSkill2 = createPostSkill(springPost2.getId(), spring.getId());
        PostSkill postSkill3 = createPostSkill(springPost3.getId(), spring.getId());
        PostSkill postSkill4 = createPostSkill(fastApiPost1.getId(), fastApi.getId());
        PostSkill postSkill5 = createPostSkill(fastApiPost2.getId(), fastApi.getId());
        PostSkill postSkill6 = createPostSkill(ktorPost1.getId(), ktor.getId());
        postSkillRepository.saveAll(List.of(postSkill1, postSkill2, postSkill3, postSkill4, postSkill5, postSkill6));
    }

    @AfterEach
    void tearDown() {
        postSkillRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        skillRepository.deleteAllInBatch();
    }

    @DisplayName("최근 N일 동안 채용 공고에서 가장 많이 쓰인 기술을 조회한다.")
    @Test
    void findTopSkillOrderByCountDesc() {
        //given
        LocalDate end = LocalDate.of(2025, 11, 7);

        //when
        var result = postSkillRepository.findTopSkillOrderByCountDesc(3, DateRange.WEEKLY, end);

        //then
        assertThat(result).hasSize(3)
            .extracting("skill.name", "count")
            .containsExactly(
                tuple(spring.getName(), 2L),
                tuple(fastApi.getName(), 1L),
                tuple(ktor.getName(), 1L)
            );
    }

    private Skill createSkill(String name) {
        return Skill.builder()
            .name(name)
            .isDeleted(false)
            .build();
    }

    private Post createPost(
        String title,
        LocalDateTime postedAt,
        LocalDateTime closeAt,
        Long companyId,
        Long roleId
    ) {
        return Post.builder()
            .title(title)
            .employmentType(EmploymentType.FULL_TIME)
            .experienceLevel(ExperienceLevel.MID_SENIOR)
            .workType(WorkType.REMOTE)
            .status(PostStatus.OPEN)
            .postedAt(postedAt)
            .closeAt(closeAt)
            .companyId(companyId)
            .roleId(roleId)
            .isDeleted(false)
            .build();
    }

    private PostSkill createPostSkill(Long postId, Long skillId) {
        return PostSkill.builder()
            .postId(postId)
            .skillId(skillId)
            .isDeleted(false)
            .build();
    }
}
