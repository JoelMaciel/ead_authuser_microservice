package com.ead.authuser.domain.repositories;

import com.ead.authuser.domain.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
