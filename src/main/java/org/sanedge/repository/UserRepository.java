package org.sanedge.repository;

import java.util.Optional;

import org.sanedge.models.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    public Optional<User> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<User> findByVerificationCode(String verificationCode) {
        return find("verificationCode", verificationCode).firstResultOptional();
    }

    @Transactional
    public User createUser(User user) {
        persist(user);
        return user;
    }

    @Transactional
    public User updateUser(User updatedUser) {
        User user = findById(updatedUser.id);
        if (user != null) {
            user.username = updatedUser.username;
            user.email = updatedUser.email;
            user.password = updatedUser.password;
            user.verificationCode = updatedUser.verificationCode;
            user.verified = updatedUser.verified;
            user.roles = updatedUser.roles;
            persist(user);
        }
        return user;
    }

    @Transactional
    public boolean deleteUser(Long id) {
        User user = findById(id);
        if (user != null) {
            delete(user);
            return true;
        }
        return false;
    }
}
