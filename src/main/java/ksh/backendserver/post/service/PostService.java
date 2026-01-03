package ksh.backendserver.post.service;

import ksh.backendserver.common.exception.CustomException;
import ksh.backendserver.common.exception.ErrorCode;
import ksh.backendserver.jobfield.repository.JobFieldRepository;
import ksh.backendserver.post.dto.projection.PostWithCompany;
import ksh.backendserver.post.dto.request.PostRequestDto;
import ksh.backendserver.post.model.PostDashboardCard;
import ksh.backendserver.post.model.PostDetail;
import ksh.backendserver.post.model.PostInfo;
import ksh.backendserver.post.repository.PostRepository;
import ksh.backendserver.skill.repository.PostSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostSkillRepository postSkillRepository;
    private final JobFieldRepository jobFieldRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public Page<PostInfo> findCompetitorPosts(
        PostRequestDto dto,
        Pageable pageable
    ) {
        if (dto.getJobFieldName() != null && !dto.getJobFieldName().isBlank()) {
            jobFieldRepository.findByName(dto.getJobFieldName())
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_FIELD_NOT_FOUND));
        }

        return postRepository
            .findByFilters(dto, pageable, LocalDateTime.now(clock))
            .map(post -> PostInfo.from(post, LocalDate.now(clock)));
    }

    @Transactional(readOnly = true)
    public PostDetail getPostDetail(Long postId) {
        var postData = postRepository.getByIdWithCompanyAndRole(postId);
        var skillNames = postSkillRepository.findSkillNamesByPostId(postId);

        return PostDetail.from(postData, skillNames, LocalDate.now(clock));
    }

    @Transactional(readOnly = true)
    public List<PostWithCompany> findNewPostsAfter(LocalDateTime checkpoint) {
        return postRepository.findByCrawledAtAfterCheckpoint(checkpoint);
    }

    @Transactional(readOnly = true)
    public List<PostDashboardCard> getRecentCompetitorPosts(int limit) {
        return postRepository
            .findWithCompanyAndJobRoleOrderByRegisteredAtDesc(limit, LocalDateTime.now(clock))
            .stream()
            .map(PostDashboardCard::from)
            .toList();
    }
}
