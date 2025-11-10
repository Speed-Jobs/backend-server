package ksh.backendserver.post.repository;

import ksh.backendserver.post.dto.projection.PostWithCompany;

import java.util.List;

public interface PostQueryRepository {

    List<PostWithCompany> findByIdInOrderByCreatedAtDesc(List<Long> companyIds, int size);
}
