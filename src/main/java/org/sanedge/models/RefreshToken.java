package org.sanedge.models;

import java.time.Instant;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "refresh_token")
public class RefreshToken extends PanacheEntity {
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    public User user;

    @Column(nullable = false, unique = true)
    public String token;

    @Column(nullable = false)
    public Instant expiryDate;
}