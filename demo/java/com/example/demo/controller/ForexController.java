package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.ForexRate;
import com.example.demo.service.ForexService;

import java.util.List;

@RestController
@RequestMapping("/fx")
public class ForexController {

    private final ForexService forexService;
    private static final Logger logger = LoggerFactory.getLogger(ForexController.class);
    @Autowired
    public ForexController(ForexService forexService) {
        this.forexService = forexService;
    }

    @GetMapping("/{targetCurrency}")
    public ResponseEntity<ForexRate> getForexRate(@PathVariable String targetCurrency) {
    	System.out.println();
    	logger.info("Recived targetCurrency {}",targetCurrency);
    	ForexRate rate = forexService.getLatestRate(targetCurrency);
        return ResponseEntity.ok(rate);
    }
    
    @GetMapping
    public ResponseEntity<List<ForexRate>> getForexRates(@RequestParam(required = false) String targetCurrency) {
    	logger.info("Recived targetCurrency {}",targetCurrency);
        List<ForexRate> rates = forexService.getLatestRates(targetCurrency);
        return ResponseEntity.ok(rates);
    }

}