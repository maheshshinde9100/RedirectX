package com.mahesh.RedirectX.repository;

import com.mahesh.RedirectX.entity.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    
    List<Analytics> findByUrlId(Long urlId);
    
    List<Analytics> findByShortCode(String shortCode);
    
    @Query("SELECT a FROM Analytics a WHERE a.urlId = :urlId AND a.clickedAt BETWEEN :startDate AND :endDate")
    List<Analytics> findByUrlIdAndDateRange(
        @Param("urlId") Long urlId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT COUNT(a) FROM Analytics a WHERE a.urlId = :urlId")
    long countByUrlId(@Param("urlId") Long urlId);
    
    @Query("SELECT COUNT(a) FROM Analytics a WHERE a.shortCode = :shortCode")
    long countByShortCode(@Param("shortCode") String shortCode);
    
    @Query("SELECT a.country as country, COUNT(a) as count FROM Analytics a WHERE a.urlId = :urlId GROUP BY a.country")
    List<Map<String, Object>> getClicksByCountry(@Param("urlId") Long urlId);
    
    @Query("SELECT a.city as city, COUNT(a) as count FROM Analytics a WHERE a.urlId = :urlId GROUP BY a.city")
    List<Map<String, Object>> getClicksByCity(@Param("urlId") Long urlId);
    
    @Query("SELECT a.deviceType as deviceType, COUNT(a) as count FROM Analytics a WHERE a.urlId = :urlId GROUP BY a.deviceType")
    List<Map<String, Object>> getClicksByDevice(@Param("urlId") Long urlId);
    
    @Query("SELECT a.browser as browser, COUNT(a) as count FROM Analytics a WHERE a.urlId = :urlId GROUP BY a.browser")
    List<Map<String, Object>> getClicksByBrowser(@Param("urlId") Long urlId);
    
    @Query("SELECT DATE(a.clickedAt) as date, COUNT(a) as count FROM Analytics a WHERE a.urlId = :urlId GROUP BY DATE(a.clickedAt) ORDER BY date DESC")
    List<Map<String, Object>> getClicksByDate(@Param("urlId") Long urlId);
}
