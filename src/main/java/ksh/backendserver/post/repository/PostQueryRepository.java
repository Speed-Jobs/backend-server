package ksh.backendserver.post.repository;

import ksh.backendserver.post.dto.projection.PostDashboardProjection;
import ksh.backendserver.post.dto.projection.PostWithCompany;
import ksh.backendserver.post.dto.projection.PostWithCompanyAndRole;
import ksh.backendserver.post.dto.request.PostRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PostQueryRepository {

    Page<PostWithCompanyAndRole> findByFilters(PostRequestDto postRequestDto, Pageable pageable, LocalDateTime now);

    PostWithCompanyAndRole getByIdWithCompanyAndRole(Long postId);

    List<PostWithCompany> findByCrawledAtAfterCheckpoint(LocalDateTime checkpoint);

    List<PostDashboardProjection> findWithCompanyAndIndustryOrderByRegisteredAtDesc(int limit, LocalDateTime now);
}
