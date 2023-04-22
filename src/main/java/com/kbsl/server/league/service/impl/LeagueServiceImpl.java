package com.kbsl.server.league.service.impl;

import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.boot.util.DiscordUtils;
import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.league.domain.repository.LeagueRepository;
import com.kbsl.server.league.dto.request.LeagueSaveRequestDto;
import com.kbsl.server.league.dto.response.LeagueDeatilResponseDto;
import com.kbsl.server.league.dto.response.LeagueResponseDto;
import com.kbsl.server.league.enums.LeagueStatusType;
import com.kbsl.server.league.service.LeagueService;
import com.kbsl.server.score.domain.repository.ScoreRepository;
import com.kbsl.server.score.dto.response.ScoreResponseDto;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.domain.repository.SongRepository;
import com.kbsl.server.song.dto.response.SongResponseDto;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.domain.repository.UserRepository;
import com.kbsl.server.user.service.principal.PrincipalUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class LeagueServiceImpl implements LeagueService {

    private final DiscordUtils discordUtils;
    private final LeagueRepository leagueRepository;
    private final SongRepository songRepository;
    private final ScoreRepository scoreRepository;
    private final UserRepository userRepository;

    /**
     * 리그를 생성한다.
     * 단, 유저의 정보가 조회되지않을 경우 예외를 발생시킨다.
     *
     * @param steamId
     * @param leagueSaveRequestDto
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public LeagueResponseDto createLeague(String steamId, LeagueSaveRequestDto leagueSaveRequestDto) throws Exception {
        /**
         * 유저에 대한 정보 및 추가 정보를 수정한다.
         */
        User userEntity = userRepository.findBySteamId(steamId)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));

        League leagueEntity = leagueRepository.save(leagueSaveRequestDto.toEntity(userEntity));

        discordUtils.LeagueCreateMessage(leagueSaveRequestDto.getLeagueName() + " 리그가 시작되었습니다!! \n" +
            "리그는 " + leagueSaveRequestDto.getLeagueStartDtime().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")) + " 부터 시작합니다!! \n" +
            "https://www.kbsl.dev/league/detail?" + leagueEntity.getSeq());

        return LeagueResponseDto.builder().entity(leagueEntity).build();
    }

    /**
     * 전체 리그를 조회한다. - Pagination
     * 현재 리그의 상태를 함께 전송한다.
     *
     * @param page
     * @param leagueStatusType
     * @param sort
     * @param elementCnt
     * @return
     */
    @Override
    @Transactional
    public Page<LeagueResponseDto> findLeagues(Integer page, LeagueStatusType leagueStatusType, String sort, Integer elementCnt) {

        // 페이징 객체를 생성한다.
        Pageable pageable = PageRequest.of(page-1, elementCnt == null ? 10 : elementCnt);
        Page<League> leagues = leagueRepository.findAllLeagueWithPage(pageable, leagueStatusType, sort);
        return leagues.map(this::convertToLeagueResponseDto);
    }

    private LeagueResponseDto convertToLeagueResponseDto(League league) {
        String status = "진행중";

        if (LocalDateTime.now().isBefore(league.getLeagueStartDtime())) {
            status = "대기중";
        } else if (LocalDateTime.now().isAfter(league.getLeagueEndDtime())) {
            status = "종료";
        }

        return LeagueResponseDto.builder()
                .entity(league)
                .leagueStatus(status)
                .build();
    }

    /**
     * 리그를 상세하게 조회한다.
     * 단, 리그가 존재하지않을 경우 예외를 발생시킨다.
     * @param leagueSeq
     * @return
     */
    @Override
    @Transactional
    public LeagueDeatilResponseDto findLeagueDetail(Long leagueSeq) {
        League leagueEntity = leagueRepository.findBySeq(leagueSeq)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 리그를 찾을 수 없습니다."));

        LeagueDeatilResponseDto responseDto = LeagueDeatilResponseDto.builder().entity(leagueEntity).build();

        return responseDto;
    }

    /**
     * 리그 시퀀스와 노래 시퀀스로 해당 리그안에 노래의 스코어정보를 조회한다.
     * 단, 리그가 존재하지 않거나 노래가 존재하지않을 경우 예외를 발생시킨다.
     * @param leagueSeq
     * @param songSeq
     * @param page
     * @param sort
     * @param elementCnt
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public Page<ScoreResponseDto> findLeagueSongScore(Long leagueSeq, Long songSeq, Integer page, String sort, Integer elementCnt) throws Exception {
        League leagueEntity = leagueRepository.findBySeq(leagueSeq)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 리그를 찾을 수 없습니다. leagueSeq =" + leagueSeq));

        Song songEntity = songRepository.findBySeq(songSeq)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 노래를 찾을 수 없습니다. songSeq =" + songSeq));

        LocalDateTime startDate = leagueEntity.getLeagueStartDtime();
        LocalDateTime endDate = leagueEntity.getLeagueEndDtime();

        Pageable pageable = PageRequest.of(page-1, elementCnt == null ? 10 : elementCnt);

        return scoreRepository.findAllScoreBySongSeqAndLeagueDateWithPage(songSeq, startDate, endDate, pageable, sort)
            .map(test -> ScoreResponseDto.builder().entity(test).build());
    }
}
