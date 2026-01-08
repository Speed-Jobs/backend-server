package ksh.backendserver.post.service;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.company.enums.CompanySize;
import ksh.backendserver.company.enums.Domain;
import ksh.backendserver.company.repository.CompanyRepository;
import ksh.backendserver.jobrole.entity.JobRole;
import ksh.backendserver.jobrole.repository.JobRoleRepository;
import ksh.backendserver.jobfield.entity.JobField;
import ksh.backendserver.jobfield.repository.JobFieldRepository;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.post.model.MatchablePost;
import ksh.backendserver.post.repository.PostRepository;
import ksh.backendserver.skill.entity.PostSkill;
import ksh.backendserver.skill.entity.Skill;
import ksh.backendserver.skill.repository.PostSkillRepository;
import ksh.backendserver.skill.repository.SkillRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("test")
class MatchablePostServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostSkillRepository postSkillRepository;

    @Autowired
    private JobRoleRepository jobRoleRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private JobFieldRepository jobFieldRepository;

    @Autowired
    private MatchablePostService matchablePostService;

    private static final LocalDateTime CHECKPOINT = LocalDateTime.of(2024, 1, 1, 0, 0);

    private Company company1;
    private Company company2;
    private Skill skill1;
    private Skill skill2;
    private JobRole jobRole1;
    private JobRole jobRole2;

    @BeforeEach
    void setUp() {
        JobField jobField1 = JobField.builder()
            .name("백엔드")
            .build();
        JobField jobField2 = JobField.builder()
            .name("프론트엔드")
            .build();
        jobField1 = jobFieldRepository.save(jobField1);
        jobField2 = jobFieldRepository.save(jobField2);

        company1 = Company.builder()
            .name("테스트 회사")
            .domain(Domain.FINTECH)
            .size(CompanySize.MEDIUM)
            .build();
        company1 = companyRepository.save(company1);

        company2 = Company.builder()
            .name("테스트 회사2")
            .domain(Domain.FINTECH)
            .size(CompanySize.LARGE)
            .build();
        company2 = companyRepository.save(company2);

        skill1 = Skill.builder()
            .name("Java")
            .build();
        skill1 = skillRepository.save(skill1);

        skill2 = Skill.builder()
            .name("React")
            .build();
        skill2 = skillRepository.save(skill2);

        jobRole1 = JobRole.builder()
            .name("백엔드 개발")
            .jobFieldId(jobField1.getId())
            .build();
        jobRole1 = jobRoleRepository.save(jobRole1);

        jobRole2 = JobRole.builder()
            .name("프론트엔드 개발")
            .jobFieldId(jobField2.getId())
            .build();
        jobRole2 = jobRoleRepository.save(jobRole2);
    }

    @AfterEach
    void tearDown() {
        postSkillRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        jobRoleRepository.deleteAllInBatch();
        skillRepository.deleteAllInBatch();
        companyRepository.deleteAllInBatch();
        jobFieldRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("체크포인트 이후 공고 리스트를 반환한다")
    void findNewMatchablePostsAfter1() {
        // given
        Post post1 = Post.builder()
            .title("백엔드 개발자 - 경계이전")
            .companyId(company1.getId())
            .jobRoleId(jobRole1.getId())
            .crawledAt(CHECKPOINT.minusSeconds(1))
            .build();
        postRepository.save(post1);

        Post post2 = Post.builder()
            .title("백엔드 개발자 - 경계")
            .companyId(company1.getId())
            .jobRoleId(jobRole1.getId())
            .crawledAt(CHECKPOINT)
            .build();
        post2 = postRepository.save(post2);

        Post post3 = Post.builder()
            .title("프론트엔드 개발자")
            .companyId(company2.getId())
            .jobRoleId(jobRole2.getId())
            .crawledAt(CHECKPOINT.plusSeconds(1))
            .build();
        post3 = postRepository.save(post3);

        PostSkill postSkill1 = PostSkill.builder()
            .postId(post2.getId())
            .skillId(skill1.getId())
            .build();
        postSkillRepository.save(postSkill1);

        PostSkill postSkill2 = PostSkill.builder()
            .postId(post3.getId())
            .skillId(skill2.getId())
            .build();
        postSkillRepository.save(postSkill2);

        // when
        List<MatchablePost> result = matchablePostService.findNewMatchablePostsAfter(CHECKPOINT);

        // then
        assertThat(result)
            .hasSize(2)
            .extracting(
                mp -> mp.getPost().getId(),
                mp -> mp.getCompany().getName(),
                mp -> mp.getJobRole().getName(),
                mp -> mp.getSkills().size()
            )
            .containsExactly(
                tuple(post2.getId(), "테스트 회사", "백엔드 개발", 1),
                tuple(post3.getId(), "테스트 회사2", "프론트엔드 개발", 1)
            );
    }

    @Test
    @DisplayName("체크포인트 이후 공고가 없으면 빈 리스트를 반환한다")
    void findNewMatchablePostsAfter_공고없음_빈리스트반환() {
        // given
        Post post1 = Post.builder()
            .title("백엔드 개발자 - 경계이전")
            .companyId(company1.getId())
            .jobRoleId(jobRole1.getId())
            .crawledAt(CHECKPOINT.minusSeconds(1))
            .build();
        postRepository.save(post1);

        PostSkill postSkill1 = PostSkill.builder()
            .postId(post1.getId())
            .skillId(skill1.getId())
            .build();
        postSkillRepository.save(postSkill1);

        // when
        List<MatchablePost> result = matchablePostService.findNewMatchablePostsAfter(CHECKPOINT);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("공고 ID로 구독 정보와 매칭될 가능성이 있는 공고를 조회한다")
    void findMatchablePostById_성공() {
        // given
        Post post = Post.builder()
            .title("백엔드 개발자")
            .companyId(company1.getId())
            .jobRoleId(jobRole1.getId())
            .crawledAt(CHECKPOINT)
            .build();
        post = postRepository.save(post);

        PostSkill postSkill = PostSkill.builder()
            .postId(post.getId())
            .skillId(skill1.getId())
            .build();
        postSkillRepository.save(postSkill);

        // when
        MatchablePost result = matchablePostService.findMatchablePostById(post.getId());

        // then
        assertThat(result.getPost().getId()).isEqualTo(post.getId());
        assertThat(result.getPost().getTitle()).isEqualTo("백엔드 개발자");
        assertThat(result.getCompany().getName()).isEqualTo("테스트 회사");
        assertThat(result.getJobRole().getName()).isEqualTo("백엔드 개발");
        assertThat(result.getSkills()).hasSize(1);
        assertThat(result.getSkills().getFirst().getSkill().getName()).isEqualTo("Java");
    }
}
