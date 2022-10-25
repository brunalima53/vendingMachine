package com.vendingMachine.service;

import com.vendingMachine.model.User;
import com.vendingMachine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CustomUserDetailsService  implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = Optional.ofNullable(userRepository.findByUsername(username)).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorityListSeller = AuthorityUtils.createAuthorityList("ROLE_SELLER");
        List<GrantedAuthority> authorityListBuyer= AuthorityUtils.createAuthorityList("ROLE_BUYER");
        // checks the boolean is seller of user to attribute adequate authority list
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),user.isSeller()? authorityListSeller : authorityListBuyer);
    }

}
