package com.cos.security2.auth;

import com.cos.security2.model.Users;
import com.cos.security2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername 동작");
        Users byUsername = userRepository.findByUsername(username);
        System.out.println("userEntity = " + byUsername);
        return new PrincipalDetails(byUsername);
    }
}
