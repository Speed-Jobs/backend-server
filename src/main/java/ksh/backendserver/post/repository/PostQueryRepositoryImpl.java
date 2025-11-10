package ksh.backendserver.post.repository;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ksh.backendserver.post.dto.projection.PostWithCompany;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static ksh.backendserver.company.entity.QCompany.company;
import static ksh.backendserver.post.entity.QPost.post;

@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<PostWithCompany> findByIdInOrderByCreatedAtDesc(
        List<Long> companyIds,
        int size
    ) {
        return queryFactory
            .select(Projections.constructor(
                PostWithCompany.class,
                post,
                company
            ))
            .from(post)
            .join(company)
                .on(post.companyId.eq(company.id))
            .where(post.companyId.in(companyIds))
            .limit(size)
            .fetch();
    }
}
