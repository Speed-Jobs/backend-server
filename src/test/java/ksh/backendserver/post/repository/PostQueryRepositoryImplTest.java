package ksh.backendserver.post.repository;

import ksh.backendserver.common.exception.CustomException;
import ksh.backendserver.common.exception.ErrorCode;
import ksh.backendserver.company.entity.Company;
import ksh.backendserver.company.repository.CompanyRepository;
import ksh.backendserver.post.dto.projection.PostWithCompanyAndRole;
import ksh.backendserver.post.dto.request.PostRequestDto;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.post.enums.EmploymentType;
import ksh.backendserver.post.enums.ExperienceLevel;
import ksh.backendserver.post.enums.PostSortCriteria;
import ksh.backendserver.post.enums.WorkType;
import ksh.backendserver.jobfield.entity.JobField;
import ksh.backendserver.jobfield.repository.JobFieldRepository;
import ksh.backendserver.jobrole.entity.JobRole;
import ksh.backendserver.jobrole.repository.JobRoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PostQueryRepositoryImplTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobRoleRepository jobRoleRepository;

    @Autowired
    private JobFieldRepository jobFieldRepository;

    private Company company1;
    private Company company2;
    private JobField jobField;
    private JobRole jobRole1;
    private JobRole jobRole2;
    private Post post1;
    private Post post2;
    private Post post3;
    private Post post4;

    private LocalDateTime baseTime;

    @BeforeEach
    void setUp() {
        baseTime = LocalDateTime.of(2025, 11, 10, 0, 0, 0);

        company1 = createCompany("Apple");
        company2 = createCompany("Google");

        jobField = createJobField("Engineering");

        jobRole1 = createJobRole("Backend Engineer", jobField.getId());
        jobRole2 = createJobRole("Frontend Engineer", jobField.getId());

        post1 = createPost(
            "공고1 제목", ExperienceLevel.SENIOR, EmploymentType.FULL_TIME,
            baseTime.minusDays(2), baseTime.plusDays(13).minusSeconds(1), company1.getId(), jobRole1.getId()
        );

        post2 = createPost(
            "공고2 제목", ExperienceLevel.ENTRY, EmploymentType.CONTRACT,
            baseTime.minusDays(5), baseTime.plusDays(10).minusSeconds(1), company2.getId(), jobRole2.getId()
        );

        post3 = createPost(
            "공고3 제목", ExperienceLevel.MID_SENIOR, EmploymentType.CONTRACT,
            baseTime.minusDays(1), baseTime.plusDays(14).minusSeconds(1), company1.getId(), jobRole1.getId()
        );

        post4 = createPost(
            "공고4 제목", ExperienceLevel.SENIOR, EmploymentType.FULL_TIME,
            baseTime.minusDays(3), baseTime.plusDays(12).minusSeconds(1), company2.getId(), jobRole2.getId()
        );
    }

    @AfterEach
    void tearDown() {
        postRepository.deleteAllInBatch();
        jobRoleRepository.deleteAllInBatch();
        jobFieldRepository.deleteAllInBatch();
        companyRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("회사 이름으로 필터링 후 등록일로 정렬해 조회한다.")
    void findByFilters_Success() {
        // given
        var postRequestDto = new PostRequestDto(
            PostSortCriteria.POST_AT,
            false,
            List.of("Apple"),
            null,
            null,
            null,
            null
        );
        Pageable pageable = PageRequest.of(0, 10);

        // when
        var result = postRepository.findByFilters(
            postRequestDto,
            pageable,
            baseTime
        );

        // then
        assertThat(result)
            .hasSize(2)
            .extracting(PostWithCompanyAndRole::getPost)
            .extracting(Post::getTitle, Post::getCompanyId)
            .containsExactly(
                tuple("공고3 제목", post3.getCompanyId()),
                tuple("공고1 제목", post1.getCompanyId())
            );
    }

    @Test
    @DisplayName("등록일이 현재 시각 이전이고 마감일이 현재 시각 이후인 공고를 마감 임박 순으로 정렬해 조회한다.")
    void findByFilters_SortByLeftDaysAscending() {
        // given
        var postRequestDto = new PostRequestDto(
            PostSortCriteria.LEFT_DAYS,
            true,
            null,
            null,
            null,
            null,
            null
        );
        Pageable pageable = PageRequest.of(0, 10);

        // when
        var result = postRepository.findByFilters(
            postRequestDto,
            pageable,
            LocalDateTime.of(2025, 11, 20, 0, 0)
        );

        // then
        assertThat(result).hasSize(3)
            .extracting("post.title", "company.name")
            .containsExactly(
                tuple("공고4 제목", "Google"),
                tuple("공고1 제목", "Apple"),
                tuple("공고3 제목", "Apple")
            );
    }

    @Test
    @DisplayName("id로 공고 상세 정보를 조회한다")
    void getByIdWithCompanyAndRole_Success() {
        // given
        Long postId = post1.getId();

        // when
        var result = postRepository.getByIdWithCompanyAndRole(postId);

        // then
        assertThat(result.getPost().getTitle()).isEqualTo("공고1 제목");
        assertThat(result.getCompany().getId()).isEqualTo(company1.getId());
        assertThat(result.getJobRole().getId()).isEqualTo(jobRole1.getId());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 공고 조회하면 예외가 발생한다.")
    void getByIdWithCompanyAndRole_NotFound() {
        // given
        Long nonExistentPostId = 99999L;

        // when & then
        assertThatThrownBy(() -> postRepository.getByIdWithCompanyAndRole(nonExistentPostId))
            .isInstanceOf(CustomException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.POST_NOT_FOUND);
    }

    private Company createCompany(String name) {
        return companyRepository.save(Company.builder()
            .name(name)
            .description("Tech Company")
            .location("USA")
            .isDeleted(false)
            .build());
    }

    private JobField createJobField(String name) {
        return jobFieldRepository.save(JobField.builder()
            .name(name)
            .description("Job Field Description")
            .isDeleted(false)
            .build());
    }

    private JobRole createJobRole(String name, Long jobFieldId) {
        return jobRoleRepository.save(JobRole.builder()
            .name(name)
            .description("Job Role Description")
            .jobFieldId(jobFieldId)
            .isDeleted(false)
            .build());
    }

    private Post createPost(
        String title, ExperienceLevel experienceLevel, EmploymentType employmentType,
        LocalDateTime postedAt, LocalDateTime closeAt, Long companyId, Long roleId
    ) {
        return postRepository.save(Post.builder()
            .title(title)
            .employmentType(employmentType.name())
            .experience(experienceLevel.name())
            .workType(WorkType.ON_SITE.name())
            .postedAt(postedAt)
            .closeAt(closeAt)
            .companyId(companyId)
            .jobRoleId(roleId)
            .sourceUrl("url")
            .isDeleted(false)
            .build());
    }
}
