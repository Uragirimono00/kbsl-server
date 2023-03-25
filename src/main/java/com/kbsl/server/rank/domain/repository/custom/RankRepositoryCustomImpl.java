package com.kbsl.server.rank.domain.repository.custom;

import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.rank.domain.model.QRank;
import com.kbsl.server.rank.domain.model.Rank;
import com.kbsl.server.rank.enums.RankProcessType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static com.kbsl.server.league.enums.LeagueStatusType.*;
import static com.kbsl.server.league.enums.LeagueStatusType.TYPE_PROCESS;
import static com.kbsl.server.rank.enums.RankProcessType.*;
import static com.kbsl.server.rank.enums.RankProcessType.TYPE_RANK;
import static java.time.LocalDateTime.now;

@RequiredArgsConstructor
public class RankRepositoryCustomImpl implements RankRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private QRank rank = QRank.rank;

    @Override
    public Page<Rank> findAllRankWithPage(Pageable pageable, RankProcessType rankProcessType, String sort) {
        List<Rank> results = jpaQueryFactory.selectFrom(rank)
            .where(
                rankProcessType == TYPE_RANK ? rank.rankProcessType.eq(TYPE_RANK) : null,
                rankProcessType == TYPE_NOMINATED ? rank.rankProcessType.eq(TYPE_NOMINATED) : null,
                rankProcessType == TYPE_UNRANK ? rank.rankProcessType.eq(TYPE_UNRANK) : null
            )
            .orderBy(orderBySort(sort))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long totalCount = jpaQueryFactory.select(rank.count())
            .from(rank)
            .where(
                rankProcessType == TYPE_RANK ? rank.rankProcessType.eq(TYPE_RANK) : null,
                rankProcessType == TYPE_NOMINATED ? rank.rankProcessType.eq(TYPE_NOMINATED) : null,
                rankProcessType == TYPE_UNRANK ? rank.rankProcessType.eq(TYPE_UNRANK) : null
            )
            .fetchOne();

        return new PageImpl<>(results, pageable, totalCount);
    }

    /**
     * 정렬 순 Order Specifier
     * 기본 동작은 최신순으로 동작한다.
     *
     * @param sort
     * @return
     */
    public OrderSpecifier<LocalDateTime> orderBySort(String sort) {
        if (sort != null) {
            switch (sort) {
                case "latest":
                    return rank.createdDtime.desc();
                case "old":
                    return rank.createdDtime.asc();
                default:
                    return rank.createdDtime.desc();
            }
        }
        return rank.createdDtime.desc();
    }
}
