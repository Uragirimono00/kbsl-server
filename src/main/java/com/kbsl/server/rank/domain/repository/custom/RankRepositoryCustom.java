package com.kbsl.server.rank.domain.repository.custom;

import com.kbsl.server.rank.domain.model.Rank;
import com.kbsl.server.rank.enums.RankProcessType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RankRepositoryCustom {
    Page<Rank> findAllRankWithPage(Pageable pageable, RankProcessType rankProcessType, String sort);
}
