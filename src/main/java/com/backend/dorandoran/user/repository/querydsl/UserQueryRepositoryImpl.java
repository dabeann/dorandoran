package com.backend.dorandoran.user.repository.querydsl;

import com.backend.dorandoran.user.domain.entity.QUser;
import com.backend.dorandoran.user.domain.entity.QUserToken;
import com.backend.dorandoran.user.domain.entity.UserToken;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QUser user = QUser.user;
    private final QUserToken userToken = QUserToken.userToken;

    @Override
    public Optional<UserToken> findUserTokenByPhoneNumber(String phoneNumber) {
        return Optional.ofNullable(
                jpaQueryFactory.select(Projections.constructor(UserToken.class,
                            userToken.id, userToken.userId, userToken.refreshToken
                        ))
                        .from(user)
                        .leftJoin(userToken).on(user.id.eq(userToken.userId))
                        .where(user.phoneNumber.eq(phoneNumber))
                        .fetchOne()
        );
    }
}