package ksh.backendserver.post.service;

import ksh.backendserver.company.entity.Company;
import ksh.backendserver.post.dto.projection.PostWithCompanyAndRole;
import ksh.backendserver.post.entity.Post;
import ksh.backendserver.post.enums.ExperienceLevel;
import ksh.backendserver.post.model.PostSummary;
import ksh.backendserver.post.repository.PostRepository;
import ksh.backendserver.role.entity.JobRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private Clock clock;

    @Test
    @DisplayName("회사 ID 목록으로 최근 공고 요약 정보를 조회한다")
    void findRecentPostSummaries() {
        // given
        Clock fixedClock = Clock.fixed(
            Instant.parse("2025-11-10T00:00:00Z"),
            ZoneId.systemDefault()
        );
        given(clock.instant()).willReturn(fixedClock.instant());
        given(clock.getZone()).willReturn(fixedClock.getZone());


        List<Long> companyIds = List.of(1L, 2L, 3L);
        int size = 1;

        Company company = createCompany(2L, "네이버");

        JobRole role = createJobRole(2L, "프론트엔드 개발자");

        Post post = createPost(
            2L, 2L, 2L,
            "React 프론트엔드 개발자 모집",
            ExperienceLevel.JUNIOR,
            LocalDateTime.of(2025, 11, 1, 10, 0),
            LocalDateTime.of(2025, 12, 12, 23, 59)
        );

        List<PostWithCompanyAndRole> data = List.of(
            new PostWithCompanyAndRole(post, company, role)
        );

        given(postRepository.findByIdInOrderByCreatedAtDesc(anyList(), anyInt(), any()))
            .willReturn(data);

        // when
        List<PostSummary> result = postService.findRecentPostSummaries(companyIds, size);

        // then
        assertThat(result).hasSize(1)
            .extracting("title", "role", "experience", "daysLeft", "company.name")
            .containsExactlyInAnyOrder(
                tuple("React 프론트엔드 개발자 모집", "프론트엔드 개발자", ExperienceLevel.JUNIOR, 32, "네이버")
            );
    }

    private Company createCompany(Long id, String name) {
        return Company.builder()
            .id(id)
            .name(name)
            .build();
    }

    private JobRole createJobRole(Long id, String name) {
        return JobRole.builder()
            .id(id)
            .name(name)
            .build();
    }

    private Post createPost(Long id, Long companyId, Long roleId, String title, ExperienceLevel experience, LocalDateTime postedAt, LocalDateTime closeAt) {
        return Post.builder()
            .id(id)
            .companyId(companyId)
            .roleId(roleId)
            .title(title)
            .experienceLevel(experience)
            .postedAt(postedAt)
            .closeAt(closeAt)
            .build();
    }
}
