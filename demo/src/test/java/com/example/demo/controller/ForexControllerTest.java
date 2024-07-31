package com.example.demo.controller;

import com.example.demo.entity.ForexRate;
import com.example.demo.service.ForexService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ForexControllerTest {

    @Mock
    private ForexService forexService;

    @InjectMocks
    private ForexController forexController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetForexRate() {
        String targetCurrency = "USD";
        ForexRate mockRate = new ForexRate();
        mockRate.setSourceCurrency("EUR");
        mockRate.setTargetCurrency(targetCurrency);
        mockRate.setExchangeRate(1.0892);
        mockRate.setDate(LocalDate.now());

        when(forexService.getLatestRate(targetCurrency)).thenReturn(mockRate);

        ResponseEntity<ForexRate> responseEntity = forexController.getForexRate(targetCurrency);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(targetCurrency, responseEntity.getBody().getTargetCurrency());
        assertEquals("EUR", responseEntity.getBody().getSourceCurrency());
        assertEquals(1.0892, responseEntity.getBody().getExchangeRate());

        verify(forexService, times(1)).getLatestRate(targetCurrency);
    }

    @Test
    public void testGetForexRatesWithTargetCurrency() {
        String targetCurrency = "USD";
        ForexRate mockRate = new ForexRate();
        mockRate.setSourceCurrency("EUR");
        mockRate.setTargetCurrency(targetCurrency);
        mockRate.setExchangeRate(1.0892);
        mockRate.setDate(LocalDate.now());

        when(forexService.getLatestRates(targetCurrency)).thenReturn(Arrays.asList(mockRate));

        ResponseEntity<List<ForexRate>> responseEntity = forexController.getForexRates(targetCurrency);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(1, responseEntity.getBody().size());
        assertEquals(targetCurrency, responseEntity.getBody().get(0).getTargetCurrency());
        assertEquals("EUR", responseEntity.getBody().get(0).getSourceCurrency());
        assertEquals(1.0892, responseEntity.getBody().get(0).getExchangeRate());

        verify(forexService, times(1)).getLatestRates(targetCurrency);
    }

    @Test
    public void testGetForexRatesWithoutTargetCurrency() {
        ForexRate rate1 = new ForexRate();
        rate1.setSourceCurrency("EUR");
        rate1.setTargetCurrency("USD");
        rate1.setExchangeRate(1.0892);
        rate1.setDate(LocalDate.now());

        ForexRate rate2 = new ForexRate();
        rate2.setSourceCurrency("EUR");
        rate2.setTargetCurrency("GBP");
        rate2.setExchangeRate(0.8426);
        rate2.setDate(LocalDate.now());

        List<ForexRate> mockRates = Arrays.asList(rate1, rate2);

        when(forexService.getLatestRates(null)).thenReturn(mockRates);

        ResponseEntity<List<ForexRate>> responseEntity = forexController.getForexRates(null);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(2, responseEntity.getBody().size());
        assertEquals("USD", responseEntity.getBody().get(0).getTargetCurrency());
        assertEquals("EUR", responseEntity.getBody().get(0).getSourceCurrency());
        assertEquals(1.0892, responseEntity.getBody().get(0).getExchangeRate());
        assertEquals("GBP", responseEntity.getBody().get(1).getTargetCurrency());
        assertEquals("EUR", responseEntity.getBody().get(1).getSourceCurrency());
        assertEquals(0.8426, responseEntity.getBody().get(1).getExchangeRate());

        verify(forexService, times(1)).getLatestRates(null);
    }
}