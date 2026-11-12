package kz.iitu.springlab.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
    private final AppUserRepository users;
    private final PasswordEncoder encoder;

    public RegistrationService(AppUserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Transactional
    public boolean register(String username, String password) {
        if (users.existsByUsername(username)) return false;
        return users.insertUser(username, encoder.encode(password))
                .map(id -> {
                    users.addRole(id, "USER");
                    return true;
                })
                .orElse(false);
    }
}
