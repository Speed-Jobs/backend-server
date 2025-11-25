package ksh.backendserver.post.repository;


import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ksh.backendserver.common.exception.CustomException;
import ksh.backendserver.common.exception.ErrorCode;
import ksh.backendserver.company.enums.DateRange;
import ksh.backendserver.group.enums.JobFieldCategory;
import ksh.backendserver.post.dto.projection.JobFieldCountProjection;
import ksh.backendserver.post.dto.projection.JobRoleCountProjection;
import ksh.backendserver.post.dto.projection.PostWithCompanyAndRole;
import ksh.backendserver.post.dto.request.JobFieldShareStatRequestDto;
import ksh.backendserver.post.dto.request.JobRoleShareStatRequestDto;
import ksh.backendserver.post.dto.request.PostRequestDto;
import ksh.backendserver.post.enums.PostSortCriteria;
import ksh.backendserver.role.entity.QIndustry;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static ksh.backendserver.company.entity.QCompany.company;
import static ksh.backendserver.group.entity.QPosition.position;
import static ksh.backendserver.post.entity.QPost.post;
import static ksh.backendserver.role.entity.QIndustry.*;

@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostWithCompanyAndRole> findByIdInOrderByCreatedAtDesc(
        List<Long> companyIds,
        int size,
        LocalDateTime now
    ) {
        BooleanExpression companyIdsFilter = companyIds == null || companyIds.isEmpty()
            ? null
            : post.companyId.in(companyIds);

        return queryFactory
            .select(Projections.constructor(
                PostWithCompanyAndRole.class,
                post,
                company,
                industry
            ))
            .from(post)
            .join(company)
            .on(post.companyId.eq(company.id))
            .join(industry)
            .on(post.industryId.eq(industry.id))
            .where(
                companyIdsFilter,
                post.postedAt.loe(now),
                post.closeAt.gt(now),
                post.isDeleted.eq(false)
            )
            .orderBy(post.postedAt.desc())
            .limit(size)
            .fetch();
    }

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
            .where(postSearchFilter(postRequestDto, now));

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
    public List<JobFieldCountProjection> countByFieldFilteredByFieldCategory(
        JobFieldShareStatRequestDto request,
        LocalDateTime end
    ) {
        return queryFactory
            .select(Projections.constructor(
                JobFieldCountProjection.class,
                position.id,
                position.name,
                post.id.count()
            ))
            .from(post)
            .join(company).on(post.companyId.eq(company.id))
            .join(industry).on(post.industryId.eq(industry.id))
            .join(position).on(industry.positionId.eq(position.id))
            .where(
                groupShareFilter(request, end)
            )
            .groupBy(position.name)
            .fetch();
    }

    @Override
    public List<JobRoleCountProjection> countByRoleFilteredByFieldId(
        JobRoleShareStatRequestDto request,
        long fieldId,
        LocalDateTime end
    ) {
        return queryFactory
            .select(Projections.constructor(
                JobRoleCountProjection.class,
                industry.name,
                post.id.count()
            ))
            .from(post)
            .join(company).on(post.companyId.eq(company.id))
            .join(industry).on(post.industryId.eq(industry.id))
            .where(
                roleShareFilter(request, fieldId, end)
            )
            .groupBy(industry.name)
            .fetch();
    }

    private Predicate postSearchFilter(PostRequestDto dto, LocalDateTime now) {
        BooleanExpression workTypeEq = dto.getEmploymentType() == null
            ? null
            : post.employmentType.eq(dto.getEmploymentType());

        BooleanExpression companyNamesIn = dto.getCompanyNames() == null || dto.getCompanyNames().isEmpty()
            ? null
            : company.name.in(dto.getCompanyNames());

        BooleanExpression postedAtLessOrEqualThanNow = post.postedAt.loe(now);
        BooleanExpression closeAtGreaterThanNow = post.closeAt.gt(now);
        BooleanExpression notDeleted = post.isDeleted.isFalse();

        return ExpressionUtils.allOf(
            workTypeEq,
            companyNamesIn,
            postedAtLessOrEqualThanNow,
            closeAtGreaterThanNow,
            notDeleted
        );
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

    private Predicate groupShareFilter(JobFieldShareStatRequestDto request, LocalDateTime end) {

        BooleanExpression postScope = switch (request.getScope()) {
            case ALL -> null;
            case COMPETITORS -> company.isCompetitor.isTrue();
            case SINGLE -> company.name.eq(request.getCompanyName());
        };

        DateRange dateRange = request.getDateRange();
        LocalDateTime start = end.minusDays(dateRange.getDuration());
        BooleanExpression postedInRange = post.postedAt.goe(start).and(post.postedAt.lt(end));

        JobFieldCategory groupCategory = request.getFieldCategory();
        BooleanExpression groupCategoryEquals = position.category.eq(groupCategory);

        BooleanExpression notDeleted = post.isDeleted.isFalse();

        return ExpressionUtils.allOf(
            postScope,
            postedInRange,
            groupCategoryEquals,
            notDeleted
        );
    }

    private Predicate roleShareFilter(JobRoleShareStatRequestDto request, long fieldId, LocalDateTime end) {

        BooleanExpression postScope = switch (request.getScope()) {
            case ALL -> null;
            case COMPETITORS -> company.isCompetitor.isTrue();
            case SINGLE -> company.name.eq(request.getCompanyName());
        };

        DateRange dateRange = request.getDateRange();
        LocalDateTime start = end.minusDays(dateRange.getDuration());
        BooleanExpression postedInRange = post.postedAt.goe(start).and(post.postedAt.lt(end));

        BooleanExpression fieldIdEquals = industry.positionId.eq(fieldId);

        BooleanExpression notDeleted = post.isDeleted.isFalse();

        return ExpressionUtils.allOf(
            postScope,
            postedInRange,
            fieldIdEquals,
            notDeleted
        );
    }
}
