package com.nito.api.repositories;

import com.nito.api.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
