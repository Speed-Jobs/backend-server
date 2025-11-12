package ksh.backendserver.post.repository;

import ksh.backendserver.post.dto.projection.PostWithCompanyAndRole;

import java.util.List;

public interface PostQueryRepository {

    List<PostWithCompanyAndRole> findByIdInOrderByCreatedAtDesc(List<Long> companyIds, int size);
}
