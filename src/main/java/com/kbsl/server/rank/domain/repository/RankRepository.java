package com.kbsl.server.rank.domain.repository;

import com.kbsl.server.rank.domain.model.Rank;
import com.kbsl.server.rank.domain.repository.custom.RankRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankRepository extends JpaRepository<Rank, Long>, RankRepositoryCustom {
    Optional<Rank> findBySeq(Long rankSeq);
}
