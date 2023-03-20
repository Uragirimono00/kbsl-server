package com.kbsl.server.score.domain.repository.custom;

import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.league.domain.model.QLeague;
import com.kbsl.server.score.domain.model.QScore;
import com.kbsl.server.score.domain.model.Score;
import com.kbsl.server.song.domain.model.QSong;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ScoreRepositoryCustomImpl implements ScoreRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private QScore score = QScore.score;

    @Override
    public Page<Score> findAllScoreBySongSeqWithPage(Long songSeq, Pageable pageable, String sort) {
        List<Score> results = queryFactory.selectFrom(score)
            .where(score.song.seq.eq(songSeq))
            .orderBy(score.modifiedScore.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long totalCount = queryFactory.select(score.count())
            .where(score.song.seq.eq(songSeq))
            .from(score)
            .fetchOne();

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public Page<Score> findAllScoreBySongSeqAndLeagueDateWithPage(Long songSeq, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String sort) {
        List<Score> results = queryFactory.selectFrom(score)
            .where(
                score.song.seq.eq(songSeq),
                score.timePost.after(startDate),
                score.timePost.before(endDate)
            )
            .orderBy(score.modifiedScore.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long totalCount = queryFactory.select(score.count())
            .where(
                score.song.seq.eq(songSeq),
                score.timePost.after(startDate),
                score.timePost.before(endDate)
            )
            .from(score)
            .fetchOne();

        return new PageImpl<>(results, pageable, totalCount);
    }
}
