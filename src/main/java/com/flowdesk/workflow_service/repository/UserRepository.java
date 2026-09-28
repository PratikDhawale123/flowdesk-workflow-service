package com.flowdesk.workflow_service.repository;

import com.flowdesk.workflow_service.entity.Role;
import com.flowdesk.workflow_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);
}