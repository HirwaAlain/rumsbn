package rw.rura.rums.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rw.rura.rums.enums.UserStatus;
import rw.rura.rums.module.users.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        rw.rura.rums.module.users.entity.UserEntity userEntity = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String authority = "ROLE_" + userEntity.getRole().name().toUpperCase();

        // enabled=false → DisabledException (inactive account)
        // nonLocked=false → LockedException (suspended account)
        boolean enabled   = userEntity.getStatus() == UserStatus.ACTIVE;
        boolean nonLocked = userEntity.getStatus() != UserStatus.SUSPENDED;

        return new User(
                userEntity.getEmail(),
                userEntity.getPasswordHash(),
                enabled,
                true,       // accountNonExpired
                true,       // credentialsNonExpired
                nonLocked,
                List.of(new SimpleGrantedAuthority(authority))
        );
    }
}
