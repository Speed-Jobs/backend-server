package ksh.backendserver.post.repository;


import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ksh.backendserver.common.exception.CustomException;
import ksh.backendserver.common.exception.ErrorCode;
import ksh.backendserver.post.dto.projection.PostWithCompanyAndRole;
import ksh.backendserver.post.dto.request.PostRequestDto;
import ksh.backendserver.post.enums.PostSortCriteria;
import ksh.backendserver.post.enums.PostStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static ksh.backendserver.company.entity.QCompany.company;
import static ksh.backendserver.post.entity.QPost.post;
import static ksh.backendserver.role.entity.QJobRole.jobRole;

@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostWithCompanyAndRole> findByIdInOrderByCreatedAtDesc(
        List<Long> companyIds,
        int size
    ) {
        BooleanExpression companyIdsFilter = companyIds == null || companyIds.isEmpty()
            ? null
            : post.companyId.in(companyIds);

        return queryFactory
            .select(Projections.constructor(
                PostWithCompanyAndRole.class,
                post,
                company,
                jobRole
            ))
            .from(post)
            .join(company)
                .on(post.companyId.eq(company.id))
            .join(jobRole)
                .on(post.roleId.eq(jobRole.id))
            .where(
                companyIdsFilter,
                post.status.eq(PostStatus.OPEN),
                post.isDeleted.eq(false)
            )
            .orderBy(post.postedAt.desc())
            .limit(size)
            .fetch();
    }

    @Override
    public Page<PostWithCompanyAndRole> findByFilters(
        PostRequestDto postRequestDto,
        Pageable pageable
    ) {
        List<PostWithCompanyAndRole> content = queryFactory
            .select(Projections.constructor(
                PostWithCompanyAndRole.class,
                post,
                company,
                jobRole
            ))
            .from(post)
            .join(company).on(post.companyId.eq(company.id))
            .join(jobRole).on(post.roleId.eq(jobRole.id))
            .where(
                postFilter(postRequestDto)
            )
            .orderBy(
                postOrder(postRequestDto)
            )
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(post.count())
            .from(post)
            .join(company).on(post.companyId.eq(company.id))
            .where(postFilter(postRequestDto));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public PostWithCompanyAndRole getByIdWithCompanyAndRole(Long postId) {
        PostWithCompanyAndRole result = queryFactory
            .select(Projections.constructor(
                PostWithCompanyAndRole.class,
                post,
                company,
                jobRole
            ))
            .from(post)
            .join(company).on(post.companyId.eq(company.id))
            .join(jobRole).on(post.roleId.eq(jobRole.id))
            .where(post.id.eq(postId))
            .fetchOne();

        if (result == null) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        return result;
    }

    private Predicate postFilter(PostRequestDto dto) {
        BooleanExpression workTypeEq = dto.getWorkType() == null ? null : post.workType.eq(dto.getWorkType());
        BooleanExpression companyNamesIn = dto.getCompanyNames() == null || dto.getCompanyNames().isEmpty() ? null : company.name.in(dto.getCompanyNames());
        BooleanExpression statusOpen = post.status.eq(PostStatus.OPEN);
        BooleanExpression notDeleted = post.isDeleted.isFalse();

        return ExpressionUtils.allOf(workTypeEq, companyNamesIn, statusOpen, notDeleted);
    }

    private OrderSpecifier<?>[] postOrder(PostRequestDto dto) {
        Order order = dto.getIsAscending() ? Order.ASC : Order.DESC;
        PostSortCriteria sortCriteria = dto.getSort() != null ? dto.getSort() : PostSortCriteria.POST_AT;

        OrderSpecifier<?> primaryOrder = switch (sortCriteria) {
            case POST_AT -> new OrderSpecifier<>(order, post.postedAt);
            case NAME -> new OrderSpecifier<>(order, company.name);
            case LEFT_DAYS -> new OrderSpecifier<>(order, post.closeAt);
        };

        OrderSpecifier<Long> tieBreaker = new OrderSpecifier<>(order, post.id);

        return new OrderSpecifier<?>[]{primaryOrder, tieBreaker};
    }
}
