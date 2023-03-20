package com.kbsl.server.league.domain.repository.custom;

import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.league.enums.LeagueStatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeagueRepositoryCustom {
    Page<League> findAllLeagueWithPage(Pageable pageable, LeagueStatusType leagueStatusType, String sort);
}
