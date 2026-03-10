package com.robsartin.maintainly.domain.port.in;

import com.robsartin.maintainly.domain.model.AppUser;

public interface UserResolver {

    AppUser resolveOrCreate(String username);
}
