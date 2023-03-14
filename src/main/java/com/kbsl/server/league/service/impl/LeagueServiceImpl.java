package com.kbsl.server.league.service.impl;

import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.league.domain.model.League;
import com.kbsl.server.league.domain.repository.LeagueRepository;
import com.kbsl.server.league.dto.request.LeagueSaveRequestDto;
import com.kbsl.server.league.dto.response.LeagueDeatilResponseDto;
import com.kbsl.server.league.dto.response.LeagueResponseDto;
import com.kbsl.server.league.service.LeagueService;
import com.kbsl.server.song.domain.model.Song;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeagueServiceImpl implements LeagueService {

    private final LeagueRepository leagueRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LeagueResponseDto createLeague(LeagueSaveRequestDto leagueSaveRequestDto) throws Exception {
        /**
         * 유저 정보를 가져온 후, DTO 에 삽입한다.
         */
        PrincipalUserDetail userDetail = (PrincipalUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        /**
         * 유저에 대한 정보 및 추가 정보를 수정한다.
         */
        User userEntity = userRepository.findBySeq(userDetail.getUserSeq())
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));

        League leagueEntity = leagueRepository.save(leagueSaveRequestDto.toEntity(userEntity));

        return LeagueResponseDto.builder().entity(leagueEntity).build();
    }

    @Override
    @Transactional
    public Page<LeagueResponseDto> findLeagues(Integer page, String sort, Integer elementCnt) {

        // 페이징 객체를 생성한다.
        Pageable pageable = PageRequest.of(page, elementCnt == null ? 10 : elementCnt);

        return leagueRepository.findAllLeagueWithPage(pageable, sort)
                .map(league -> LeagueResponseDto.builder().entity(league).build());
    }

    @Override
    @Transactional
    public LeagueDeatilResponseDto findLeagueDetail(Long leagueSeq) {
        League leagueEntity = leagueRepository.findBySeq(leagueSeq)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 리그를 찾을 수 없습니다."));

        LeagueDeatilResponseDto responseDto = LeagueDeatilResponseDto.builder().entity(leagueEntity).build();
        List<Song> songList = leagueEntity.getSongsList();

        if (!songList.isEmpty()) {
            List<SongResponseDto> songResponseDtoList = songList.stream()
                    .map(postFile -> SongResponseDto.builder().entity(postFile).build())
                    .collect(Collectors.toList());
            responseDto.setSongsList(songResponseDtoList);
        }

        return responseDto;
    }
}
