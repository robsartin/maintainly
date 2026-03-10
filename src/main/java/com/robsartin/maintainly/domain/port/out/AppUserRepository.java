package com.robsartin.maintainly.domain.port.out;

import java.util.Optional;

import com.robsartin.maintainly.domain.model.AppUser;

public interface AppUserRepository {

    Optional<AppUser> findByUsername(String username);

    AppUser save(AppUser user);
}
