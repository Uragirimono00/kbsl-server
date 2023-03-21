package com.kbsl.server.user.service.principal;

import com.kbsl.server.auth.enums.ERole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@ToString
@NoArgsConstructor
public class PrincipalUserDetail implements UserDetails {
    private Long userSeq;
    private String password;
    private String username;
    private String nickName;
    private ERole eRole;
    private String imageUrl;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority(eRole.name()));
        return auth;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Builder
    public PrincipalUserDetail(Long userSeq, String password, String username, String nickName, ERole eRole, String imageUrl) {
        this.userSeq = userSeq;
        this.password = password;
        this.username = username;
        this.nickName = nickName;
        this.eRole = eRole;
        this.imageUrl = imageUrl;
    }
}
