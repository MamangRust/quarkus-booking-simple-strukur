package org.sanedge.repository;

import java.util.Optional;

import org.sanedge.models.RefreshToken;
import org.sanedge.models.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RefreshTokenRepository implements PanacheRepository<RefreshToken> {
    public Optional<RefreshToken> findByToken(String token) {
        return find("token", token).firstResultOptional();
    }

    public Optional<RefreshToken> findByUser(User user) {
        return find("user", user).firstResultOptional();
    }

    public int deleteByUser(User user) {
        return (int) delete("user", user);
    }
}