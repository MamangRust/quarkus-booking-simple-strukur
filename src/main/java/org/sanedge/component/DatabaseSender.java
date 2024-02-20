package org.sanedge.component;

import org.sanedge.enums.ERole;
import org.sanedge.models.Role;
import org.sanedge.models.User;
import org.sanedge.repository.RoleRepository;
import org.sanedge.repository.UserRepository;
import org.sanedge.security.HashProvider;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DatabaseSender {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final HashProvider hashProvider;

    public DatabaseSender(UserRepository userRepository, RoleRepository roleRepository, HashProvider hashProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hashProvider = hashProvider;
    }

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        // Seed roles if they don't exist
        seedRoles();

        // Seed users if they don't exist
        seedUsers();
    }

    private void seedRoles() {
        if (roleRepository.listAll().isEmpty()) {

            roleRepository.persist(new Role(ERole.ROLE_USER));
            roleRepository.persist(new Role(ERole.ROLE_MODERATOR));
            roleRepository.persist(new Role(ERole.ROLE_ADMIN));
        }
    }

    private void seedUsers() {
        if (userRepository.listAll().isEmpty()) {
            // Create a sample user
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@example.com");
            user.setPassword(hashProvider.hashPassword("password"));
            user.setVerified(true);

            // Retrieve roles
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            user.getRoles().add(userRole);

            // Save user
            userRepository.persist(user);
        }
    }
}