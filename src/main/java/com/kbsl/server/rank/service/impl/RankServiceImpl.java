package com.kbsl.server.rank.service.impl;

import com.kbsl.server.auth.enums.ERole;
import com.kbsl.server.boot.exception.RestException;
import com.kbsl.server.rank.domain.model.Rank;
import com.kbsl.server.rank.domain.repository.RankRepository;
import com.kbsl.server.rank.dto.request.RankUpdateRequestDto;
import com.kbsl.server.rank.dto.response.RankResponseDto;
import com.kbsl.server.rank.enums.RankProcessType;
import com.kbsl.server.rank.service.RankService;
import com.kbsl.server.song.domain.model.Song;
import com.kbsl.server.song.domain.repository.SongRepository;
import com.kbsl.server.user.domain.model.User;
import com.kbsl.server.user.domain.repository.UserRepository;
import com.kbsl.server.user.enums.UserPermissionType;
import com.kbsl.server.user.service.principal.PrincipalUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static com.kbsl.server.auth.enums.ERole.ROLE_ADMIN;
import static com.kbsl.server.rank.enums.RankProcessType.TYPE_NOMINATED;
import static com.kbsl.server.user.enums.UserPermissionType.*;

@RequiredArgsConstructor
@Service
public class RankServiceImpl implements RankService {

    private final UserRepository userRepository;
    private final RankRepository rankRepository;
    private final SongRepository songRepository;

    /**
     * 특정 노래를 랭크로 지명한다.
     * 단, steamId를 가진 유저가 조회되지 않을경우 예외를 발생시킨다.
     * @param songSeq
     * @param steamId
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public RankResponseDto createSongRank(Long songSeq, String steamId) throws Exception {
        /**
         * 유저 및 리그에 대한 정보 및 추가 정보를 수정한다.
         */
        User userEntity = userRepository.findByUsername(steamId)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));

        Song songEntity = songRepository.findBySeq(songSeq)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "해당하는 노래를 찾을 수 없습니다. songSeq = " + songSeq));

        return RankResponseDto.builder().entity(rankRepository.save(Rank.builder()
            .stars(0D)
            .rankProcessType(TYPE_NOMINATED)
            .song(songEntity)
            .user(userEntity)
            .build()
        )).build();
    }

    /**
     * 모든 랭크를 조회한다.
     * @param page
     * @param rankProcessType
     * @param sort
     * @param elementCnt
     * @return
     */
    @Override
    @Transactional
    public Page<RankResponseDto> findAllRank(Integer page, RankProcessType rankProcessType, String sort, Integer elementCnt) {
        // 페이징 객체를 생성한다.
        Pageable pageable = PageRequest.of(page-1, elementCnt == null ? 10 : elementCnt);
        Page<Rank> ranks = rankRepository.findAllRankWithPage(pageable, rankProcessType, sort);

        return ranks.map(RankResponseDto::new);
    }

    /**
     * 특정 랭크를 수정한다.
     * 단, steamId를 가진 유저가 조회되지 않을경우 예외를 발생시키며 RT권한 또는 관리자 권한을 가지고 있어야한다.
     *
     * @param rankSeq
     * @param rankUpdateRequestDto
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public RankResponseDto updateRank(Long rankSeq, RankUpdateRequestDto rankUpdateRequestDto) throws Exception {
        /**
         * 요청자를 조회하여 권한을 체크한다.
         */
        PrincipalUserDetail userDetail = (PrincipalUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User userEntity = userRepository.findBySeq(userDetail.getUserSeq())
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "일치하는 유저를 찾을 수 없습니다."));

        if (userEntity.getUserPermissionList().stream().filter(userPermissionList -> userPermissionList.getPermission().getUserPermissionType().equals(TYPE_RANK) || userEntity.getERole().equals(ROLE_ADMIN)).collect(Collectors.toList()).isEmpty()){
            throw new RestException(HttpStatus.BAD_REQUEST, "RT 또는 관리자가 아닙니다.");
        }

        Rank rankEntity = rankRepository.findBySeq(rankSeq)
            .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND, "해당하는 랭크를 찾을 수 없습니다. rankSeq = " + rankSeq));

        rankEntity.update(rankUpdateRequestDto);

        return RankResponseDto.builder().entity(rankEntity).build();
    }

}
