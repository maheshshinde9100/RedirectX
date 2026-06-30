package com.mahesh.RedirectX.repository;

import com.mahesh.RedirectX.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    
    Optional<Url> findByShortCode(String shortCode);
    
    Optional<Url> findByCustomAlias(String customAlias);
    
    List<Url> findByUserId(Long userId);
    
    @Query("SELECT u FROM Url u WHERE u.userId = :userId AND u.active = true ORDER BY u.createdAt DESC")
    List<Url> findActiveUrlsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT u FROM Url u WHERE u.expiresAt < :now AND u.active = true")
    List<Url> findExpiredUrls(@Param("now") LocalDateTime now);
    
    boolean existsByShortCode(String shortCode);
    
    boolean existsByCustomAlias(String customAlias);
    
    @Query("SELECT COUNT(u) FROM Url u WHERE u.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
}
