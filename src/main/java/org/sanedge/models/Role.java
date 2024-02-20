package org.sanedge.models;

import org.sanedge.enums.ERole;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "roles")
public class Role extends PanacheEntity {
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    public ERole name;

    public ERole toERole() {
        switch (this.getName()) {
            case ERole.ROLE_USER:
                return ERole.ROLE_USER;
            case ERole.ROLE_MODERATOR:
                return ERole.ROLE_MODERATOR;
            case ERole.ROLE_ADMIN:
                return ERole.ROLE_ADMIN;
            default:
                throw new IllegalArgumentException("Unsupported role: " + this.getName());
        }
    }
}