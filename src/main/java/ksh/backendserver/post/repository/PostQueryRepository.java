package ksh.backendserver.post.repository;

import ksh.backendserver.post.dto.projection.PostWithCompanyAndRole;
import ksh.backendserver.post.dto.request.PostRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostQueryRepository {

    List<PostWithCompanyAndRole> findByIdInOrderByCreatedAtDesc(List<Long> companyIds, int size);

    Page<PostWithCompanyAndRole> findByFilters(PostRequestDto postRequestDto, Pageable pageable);

    PostWithCompanyAndRole getByIdWithCompanyAndRole(Long postId);
}
