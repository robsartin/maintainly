package com.robsartin.maintainly.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.AppUser;
import com.robsartin.maintainly.domain.port.out.AppUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAppUserRepository
        extends JpaRepository<AppUser, UUID>,
        AppUserRepository {

    @Override
    Optional<AppUser> findByUsername(String username);
}
