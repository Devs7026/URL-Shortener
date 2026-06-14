package com.dev.urlshortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.urlshortener.model.UrlMapping;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, Long> {

}
