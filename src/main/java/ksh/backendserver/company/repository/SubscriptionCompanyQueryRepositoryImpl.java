package ksh.backendserver.company.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static ksh.backendserver.company.entity.QCompany.company;
import static ksh.backendserver.company.entity.QSubscriptionCompany.subscriptionCompany;

@RequiredArgsConstructor
public class SubscriptionCompanyQueryRepositoryImpl implements SubscriptionCompanyQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findCompanyNamesByUserId(Long userId) {
        return queryFactory
            .select(company.name)
            .from(subscriptionCompany)
            .join(company).on(subscriptionCompany.companyId.eq(company.id))
            .where(subscriptionCompany.userId.eq(userId))
            .fetch();
    }
}