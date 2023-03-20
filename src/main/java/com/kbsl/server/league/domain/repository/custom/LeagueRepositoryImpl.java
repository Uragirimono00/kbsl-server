package com.kbsl.server.league.domain.repository.custom;

import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.league.domain.model.QLeague;
import com.kbsl.server.league.enums.LeagueStatusType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static com.kbsl.server.league.enums.LeagueStatusType.*;
import static java.time.LocalDateTime.now;

@Slf4j
@RequiredArgsConstructor
public class LeagueRepositoryImpl implements LeagueRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private QLeague league = QLeague.league;
    @Override
    public Page<League> findAllLeagueWithPage(Pageable pageable, LeagueStatusType leagueStatusType, String sort) {
        List<League> results = queryFactory.selectFrom(league)
                .where(
                        // todo: ㅋㅋㅋㅋ 어캐해 이거 ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ
                        leagueStatusType == TYPE_WAIT ? league.leagueStartDtime.after(now()) : null,
                        leagueStatusType == TYPE_COMPLETE ? league.leagueEndDtime.before(now()) : null,
                        leagueStatusType == TYPE_PROCESS ? league.leagueStartDtime.before(now())  : null,
                        leagueStatusType == TYPE_PROCESS ? league.leagueEndDtime.after(now())  : null
                )
                .orderBy(orderBySort(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory.select(league.count())
                .from(league)
                .fetchOne();

        return new PageImpl<>(results, pageable, totalCount);
    }

    /**
     * 정렬 순 Order Specifier
     * 기본 동작은 최신순으로 동작한다.
     * @param sort
     * @return
     */
    public OrderSpecifier<LocalDateTime> orderBySort(String sort) {
        if(sort != null) {
            switch (sort) {
                case "latest":
                    return league.createdDtime.desc();
                case "old":
                    return league.createdDtime.asc();
                default:
                    return league.createdDtime.desc();
            }
        }
        return league.createdDtime.desc();
    }
}
