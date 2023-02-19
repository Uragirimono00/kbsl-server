package com.kbsl.kbslserver.user.service.principal;

import com.kbsl.kbslserver.user.domain.model.User;
import com.kbsl.kbslserver.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public PrincipalUserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당유저를 찾을 수 없습니다."));

        PrincipalUserDetail userDetail = PrincipalUserDetail.builder()
                .userSeq(user.getSeq())
                .password(user.getPassword())
                .eRole(user.getERole())
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .build();

        return userDetail;
    }
}
