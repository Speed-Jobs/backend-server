package ksh.backendserver.post.repository;

import ksh.backendserver.post.dto.projection.GroupCountProjection;
import ksh.backendserver.post.dto.projection.PostWithCompanyAndRole;
import ksh.backendserver.post.dto.projection.RoleCountProjection;
import ksh.backendserver.post.dto.request.GroupShareStatRequestDto;
import ksh.backendserver.post.dto.request.PostRequestDto;
import ksh.backendserver.post.dto.request.RoleShareStatRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PostQueryRepository {

    List<PostWithCompanyAndRole> findByIdInOrderByCreatedAtDesc(List<Long> companyIds, int size, LocalDateTime now);

    Page<PostWithCompanyAndRole> findByFilters(PostRequestDto postRequestDto, Pageable pageable, LocalDateTime now);

    PostWithCompanyAndRole getByIdWithCompanyAndRole(Long postId);

    //TODO: 메소드 이름 변경, 집계는 Role이 아니라 Group을 기준으로임
    List<GroupCountProjection> aggregateByGroupCategoryGroupByRole(GroupShareStatRequestDto request, LocalDateTime end);

    List<RoleCountProjection> aggregateByGroupIdGroupByRole(RoleShareStatRequestDto request, long groupId, LocalDateTime end);
}
