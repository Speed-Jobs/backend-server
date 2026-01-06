package ksh.backendserver.jobfield.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static ksh.backendserver.jobfield.entity.QJobField.jobField;
import static ksh.backendserver.jobfield.entity.QSubscriptionJobField.subscriptionJobField;

@RequiredArgsConstructor
public class SubscriptionJobFieldQueryRepositoryImpl implements SubscriptionJobFieldQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findJobFieldNamesByUserId(Long userId) {
        return queryFactory
            .select(jobField.name)
            .from(subscriptionJobField)
            .join(jobField).on(subscriptionJobField.jobFieldId.eq(jobField.id))
            .where(subscriptionJobField.userId.eq(userId))
            .fetch();
    }
}