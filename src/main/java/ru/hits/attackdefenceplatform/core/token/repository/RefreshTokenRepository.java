package ru.hits.attackdefenceplatform.core.token.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.expirationDate < :now")
    void deleteByExpirationDateBefore(@Param("now") Date now);
}
