package com.kbsl.server.league.domain.repository.custom;

import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.league.domain.model.QLeague;
import com.kbsl.server.league.domain.repository.LeagueRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class LeagueRepositoryImpl implements LeagueRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private QLeague league = QLeague.league;
    @Override
    public Page<League> findAllLeagueWithPage(Pageable pageable, String sort) {
        List<League> results = queryFactory.selectFrom(league)
                .orderBy(league.createdDtime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, results.size());
    }
}
