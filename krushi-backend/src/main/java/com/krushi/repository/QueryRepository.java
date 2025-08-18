package com.krushi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface QueryRepository extends JpaRepository<QueryEntity, UUID> {}
