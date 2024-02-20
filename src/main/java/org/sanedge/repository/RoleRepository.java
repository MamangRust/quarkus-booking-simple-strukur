package org.sanedge.repository;

import java.util.Optional;

import org.sanedge.enums.ERole;
import org.sanedge.models.Role;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoleRepository implements PanacheRepository<Role> {
    public Optional<Role> findByName(ERole name) {
        return find("name", name).firstResultOptional();
    }
}