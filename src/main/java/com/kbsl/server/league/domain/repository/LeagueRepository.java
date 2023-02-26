package com.kbsl.server.league.domain.repository;

import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.league.domain.repository.custom.LeagueRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeagueRepository extends JpaRepository<League, Long>, LeagueRepositoryCustom {
    Optional<League> findBySeq(Long leagueSeq);

}
