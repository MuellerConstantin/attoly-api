package de.x1c1b.attoly.api.security;

import de.x1c1b.attoly.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class PrincipalService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public PrincipalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByEmail(username);
    }

    public Principal loadUserByEmail(String email) throws EmailNotFoundException {
        return userRepository.findByEmail(email).map(Principal::new).orElseThrow(() -> new EmailNotFoundException(email));
    }
}
