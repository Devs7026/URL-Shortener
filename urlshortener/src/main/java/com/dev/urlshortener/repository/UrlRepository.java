package com.dev.urlshortener.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dev.urlshortener.model.UrlMapping;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, Long> {

    @Modifying
    @Query("""
       UPDATE UrlMapping u
       SET u.clickCount = u.clickCount + 1
       WHERE u.id = :id
       """)
    int incrementClickCount(@Param("id") Long id);

    @Query("""
       SELECT u.longUrl
       FROM UrlMapping u
       WHERE u.id = :id
       """)
    Optional<String> findLongUrlById(@Param("id") Long id);

    Optional<UrlMapping> findByLongUrl(String longUrl);

    Optional<UrlMapping> findByCustomAlias(String customAlias);
}
