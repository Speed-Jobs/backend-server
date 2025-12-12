package ksh.backendserver.post.repository;


import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ksh.backendserver.common.exception.CustomException;
import ksh.backendserver.common.exception.ErrorCode;
import ksh.backendserver.post.dto.projection.PostDashboardProjection;
import ksh.backendserver.post.dto.projection.PostWithCompany;
import ksh.backendserver.post.dto.projection.PostWithCompanyAndRole;
import ksh.backendserver.post.dto.request.PostRequestDto;
import ksh.backendserver.post.enums.PostSortCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static ksh.backendserver.company.entity.QCompany.company;
import static ksh.backendserver.group.entity.QPosition.position;
import static ksh.backendserver.post.entity.QPost.post;
import static ksh.backendserver.post.enums.PostSortCriteria.POST_AT;
import static ksh.backendserver.role.entity.QIndustry.industry;

@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostWithCompanyAndRole> findByFilters(
        PostRequestDto postRequestDto,
        Pageable pageable,
        LocalDateTime now
    ) {
        List<PostWithCompanyAndRole> content = queryFactory
            .select(Projections.constructor(
                PostWithCompanyAndRole.class,
                post,
                company,
                industry
            ))
            .from(post)
            .join(company).on(post.companyId.eq(company.id))
            .join(industry).on(post.industryId.eq(industry.id))
            .join(position).on(industry.positionId.eq(position.id))
            .where(
                postSearchFilter(postRequestDto, now)
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
            .join(industry).on(post.industryId.eq(industry.id))
            .join(position).on(industry.positionId.eq(position.id))
            .where(
                postSearchFilter(postRequestDto, now)
            );

        return PageableExecutionUtils.getPage(
            content,
            pageable,
            countQuery::fetchOne
        );
    }

    @Override
    public PostWithCompanyAndRole getByIdWithCompanyAndRole(Long postId) {
        PostWithCompanyAndRole result = queryFactory
            .select(Projections.constructor(
                PostWithCompanyAndRole.class,
                post,
                company,
                industry
            ))
            .from(post)
            .join(company).on(post.companyId.eq(company.id))
            .join(industry).on(post.industryId.eq(industry.id))
            .where(post.id.eq(postId))
            .fetchOne();

        if (result == null) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        return result;
    }

    @Override
    public List<PostWithCompany> findByCrawledAtAfterCheckpoint(LocalDateTime checkpoint) {
        return queryFactory
            .select(Projections.constructor(
                PostWithCompany.class,
                post,
                company
            ))
            .from(post)
            .join(company).on(post.companyId.eq(company.id))
            .where(
                post.crawledAt.goe(checkpoint),
                post.isDeleted.isFalse()
            )
            .fetch();
    }

    @Override
    public List<PostDashboardProjection> findWithCompanyAndIndustryOrderByRegisteredAtDesc(
        int limit,
        LocalDateTime now
    ) {
        return queryFactory
            .select(Projections.constructor(
                PostDashboardProjection.class,
                post,
                company,
                industry,
                post.postedAt.coalesce(post.crawledAt)
            ))
            .from(post)
            .join(company).on(post.companyId.eq(company.id))
            .join(industry).on(post.industryId.eq(industry.id))
            .where(
                post.postedAt.coalesce(post.crawledAt).loe(now),
                company.isCompetitor.isTrue(),
                post.isDeleted.isFalse()
            )
            .orderBy(
                post.postedAt.coalesce(post.crawledAt).desc()
            )
            .limit(limit)
            .fetch();
    }

    private Predicate postSearchFilter(PostRequestDto dto, LocalDateTime now) {
        return ExpressionUtils.allOf(
            companyNamesIn(dto),
            postTitleContains(dto),
            postedAtInYearMonth(dto),
            postedAtLessOrEqualThanNow(now),
            positionNameEquals(dto),
            notDeleted()
        );
    }

    private OrderSpecifier<?>[] postOrder(PostRequestDto dto) {
        Order order = dto.getIsAscending() ? Order.ASC : Order.DESC;
        PostSortCriteria sortCriteria = dto.getSort() != null ? dto.getSort() : POST_AT;

        OrderSpecifier<?> primaryOrder = switch (sortCriteria) {
            case POST_AT -> new OrderSpecifier<>(order, post.postedAt.coalesce(post.crawledAt));
            case COMPANY_NAME -> new OrderSpecifier<>(order, company.name);
            case TITLE -> new OrderSpecifier<>(order, post.title);
            case LEFT_DAYS -> new OrderSpecifier<>(order, post.closeAt);
        };

        OrderSpecifier<Long> tieBreaker = new OrderSpecifier<>(order, post.id);

        return new OrderSpecifier<?>[]{primaryOrder, tieBreaker};
    }

    private BooleanExpression companyNamesIn(PostRequestDto dto) {
        if (dto.getCompanyNames() == null || dto.getCompanyNames().isEmpty()) {
            return null;
        }

        List<String> lowerCaseCompanyNames = dto.getCompanyNames().stream()
            .map(String::toLowerCase)
            .toList();

        return company.name.lower().in(lowerCaseCompanyNames);
    }

    private BooleanExpression postTitleContains(PostRequestDto dto) {
        return dto.getPostTitle() == null
            ? null
            : post.title.lower().contains(dto.getPostTitle().toLowerCase());
    }

    private BooleanExpression postedAtInYearMonth(PostRequestDto dto) {
        if (dto.getYear() == null || dto.getMonth() == null) {
            return null;
        }

        LocalDateTime startOfMonth = LocalDateTime.of(dto.getYear(), dto.getMonth(), 1, 0, 0);
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);

        var actualPostedAt = post.postedAt.coalesce(post.crawledAt);

        return actualPostedAt.goe(startOfMonth).and(actualPostedAt.lt(startOfNextMonth));
    }

    private BooleanExpression postedAtLessOrEqualThanNow(LocalDateTime now) {
        var actualPostedAt = post.postedAt.coalesce(post.crawledAt);
        return actualPostedAt.loe(now);
    }

    private BooleanExpression positionNameEquals(PostRequestDto dto) {
        return dto.getPositionName() == null
            ? null
            : position.name.lower().eq(dto.getPositionName().toLowerCase());
    }

    private BooleanExpression notDeleted() {
        return post.isDeleted.isFalse();
    }
}
