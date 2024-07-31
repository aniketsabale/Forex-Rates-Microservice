package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.ForexRate;

import java.util.List;
import java.util.Optional;

public interface ForexRepository extends JpaRepository<ForexRate, Long> {

    Optional<ForexRate> findTopByTargetCurrencyOrderByDateDesc(String targetCurrency);

    List<ForexRate> findTop3ByTargetCurrencyOrderByDateDesc(String targetCurrency);
}