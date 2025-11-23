package ksh.backendserver.post.repository;


import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ksh.backendserver.common.exception.CustomException;
import ksh.backendserver.common.exception.ErrorCode;
import ksh.backendserver.company.enums.DateRange;
import ksh.backendserver.group.enums.JobFieldCategory;
import ksh.backendserver.post.dto.projection.GroupCountProjection;
import ksh.backendserver.post.dto.projection.PostWithCompanyAndRole;
import ksh.backendserver.post.dto.projection.RoleCountProjection;
import ksh.backendserver.post.dto.request.GroupShareStatRequestDto;
import ksh.backendserver.post.dto.request.PostRequestDto;
import ksh.backendserver.post.dto.request.RoleShareStatRequestDto;
import ksh.backendserver.post.enums.PostSortCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static ksh.backendserver.company.entity.QCompany.company;
import static ksh.backendserver.group.entity.QJobField.jobField;
import static ksh.backendserver.post.entity.QPost.post;
import static ksh.backendserver.role.entity.QJobRole.jobRole;

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
                jobRole
            ))
            .from(post)
            .join(company)
            .on(post.companyId.eq(company.id))
            .join(jobRole)
            .on(post.roleId.eq(jobRole.id))
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
                jobRole
            ))
            .from(post)
            .join(company).on(post.companyId.eq(company.id))
            .join(jobRole).on(post.roleId.eq(jobRole.id))
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
    
    @Override
    public List<GroupCountProjection> countByFieldFilteredByFieldCategory(
        GroupShareStatRequestDto request,
        LocalDateTime end
    ) {
        return queryFactory
            .select(Projections.constructor(
                GroupCountProjection.class,
                jobField.id,
                jobField.name,
                post.id.count()
            ))
            .from(post)
            .join(company).on(post.companyId.eq(company.id))
            .join(jobRole).on(post.roleId.eq(jobRole.id))
            .join(jobField).on(jobRole.fieldId.eq(jobField.id))
            .where(
                groupShareFilter(request, end)
            )
            .groupBy(jobField.name)
            .fetch();

    }

    @Override
    public List<RoleCountProjection> countByRoleFilteredByFieldId(
        RoleShareStatRequestDto request,
        long groupId,
        LocalDateTime end
    ) {
        return queryFactory
            .select(Projections.constructor(
                RoleCountProjection.class,
                jobRole.id,
                jobRole.name,
                post.id.count()
            ))
            .from(post)
            .join(company).on(post.companyId.eq(company.id))
            .join(jobRole).on(post.roleId.eq(jobRole.id))
            .where(
                roleShareFilter(request, groupId, end)
            )
            .groupBy(jobRole.name)
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

    private Predicate groupShareFilter(GroupShareStatRequestDto request, LocalDateTime end) {

        BooleanExpression postScope = switch (request.getScope()) {
            case ALL -> null;
            case COMPETITORS -> company.isCompetitor.isTrue();
            case SINGLE -> company.name.eq(request.getCompanyName());
        };

        DateRange dateRange = request.getDateRange();
        LocalDateTime start = end.minusDays(dateRange.getDuration());
        BooleanExpression postedInRange = post.postedAt.goe(start).and(post.postedAt.lt(end));

        JobFieldCategory groupCategory = request.getGroupCategory();
        BooleanExpression groupCategoryEquals = jobField.category.eq(groupCategory);

        BooleanExpression notDeleted = post.isDeleted.isFalse();

        return ExpressionUtils.allOf(
            postScope,
            postedInRange,
            groupCategoryEquals,
            notDeleted
        );
    }

    private Predicate roleShareFilter(RoleShareStatRequestDto request, long groupId, LocalDateTime end) {

        BooleanExpression postScope = switch (request.getScope()) {
            case ALL -> null;
            case COMPETITORS -> company.isCompetitor.isTrue();
            case SINGLE -> company.name.eq(request.getCompanyName());
        };

        DateRange dateRange = request.getDateRange();
        LocalDateTime start = end.minusDays(dateRange.getDuration());
        BooleanExpression postedInRange = post.postedAt.goe(start).and(post.postedAt.lt(end));

        BooleanExpression groupIdEquals = jobRole.fieldId.eq(groupId);

        BooleanExpression notDeleted = post.isDeleted.isFalse();

        return ExpressionUtils.allOf(
            postScope,
            postedInRange,
            groupIdEquals,
            notDeleted
        );
    }
}
