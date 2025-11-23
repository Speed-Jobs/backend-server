package ksh.backendserver.post.repository;

import ksh.backendserver.post.dto.projection.JobFieldCountProjection;
import ksh.backendserver.post.dto.projection.PostWithCompanyAndRole;
import ksh.backendserver.post.dto.projection.JobRoleCountProjection;
import ksh.backendserver.post.dto.request.JobFieldShareStatRequestDto;
import ksh.backendserver.post.dto.request.PostRequestDto;
import ksh.backendserver.post.dto.request.JobRoleShareStatRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PostQueryRepository {

    List<PostWithCompanyAndRole> findByIdInOrderByCreatedAtDesc(List<Long> companyIds, int size, LocalDateTime now);

    Page<PostWithCompanyAndRole> findByFilters(PostRequestDto postRequestDto, Pageable pageable, LocalDateTime now);

    PostWithCompanyAndRole getByIdWithCompanyAndRole(Long postId);
    
    List<JobFieldCountProjection> countByFieldFilteredByFieldCategory(JobFieldShareStatRequestDto request, LocalDateTime end);

    List<JobRoleCountProjection> countByRoleFilteredByFieldId(JobRoleShareStatRequestDto request, long groupId, LocalDateTime end);
}
