package solutions.mystuff.domain.port.in;

import solutions.mystuff.domain.model.AppUser;

public interface UserResolver {

    AppUser resolveOrCreate(String username);
}
