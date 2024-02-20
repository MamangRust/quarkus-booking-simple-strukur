package org.sanedge.repository;

import java.util.Optional;

import org.sanedge.models.ResetToken;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ResetTokenRepository implements PanacheRepository<ResetToken> {

    public void deleteByUserId(Long userId) {
        delete("user_id", userId);
    }

    public Optional<ResetToken> findByToken(String token) {
        return find("token", token).firstResultOptional();
    }
}
