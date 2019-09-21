package me.lnsprt.projects.restblog.security.service;

import me.lnsprt.projects.restblog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAuthDetailsService implements UserDetailsService {
    private UserRepository db;

    @Autowired
    UserAuthDetailsService(UserRepository db){
        this.db = db;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if(!db.existsByNickname(username))
            throw new UsernameNotFoundException(username);

        me.lnsprt.projects.restblog.model.User user = db.findByNickname(username);
        String roles = user.getRoles()
                .stream()
                .map(role -> String.format("ROLE_%s", role))
                .collect(Collectors.joining(","));

        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(roles);

        return User.builder()
                .username(user.getNickname())
                .password(user.getPassword())
                .authorities(authorities)
                .disabled(user.isSuspended())
                .build();
    }
}
