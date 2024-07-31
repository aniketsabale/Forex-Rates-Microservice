package com.example.demo.service;

import com.example.demo.entity.ForexApiResponse;
import com.example.demo.entity.ForexApiResponseList;
import com.example.demo.entity.ForexRate;
import com.example.demo.repository.ForexRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ForexServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ForexRepository forexRepository;

    @InjectMocks
    private ForexService forexService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFetchLatestRate() {
        String targetCurrency = "USD";
        String url = "https://api.frankfurter.app/latest?to=" + targetCurrency;
        ForexRate mockRate = new ForexRate();
        mockRate.setSourceCurrency("EUR");
        mockRate.setTargetCurrency(targetCurrency);
        mockRate.setExchangeRate(1.0892);
        mockRate.setDate(LocalDate.now());

        when(restTemplate.getForEntity(eq(url), eq(ForexRate.class)))
                .thenReturn(ResponseEntity.ok(mockRate));
        when(forexRepository.save(any(ForexRate.class))).thenReturn(mockRate);

        ForexRate fetchedRate = forexService.fetchLatestRate(targetCurrency);

        assertNotNull(fetchedRate);
        assertEquals(targetCurrency, fetchedRate.getTargetCurrency());
        assertEquals("EUR", fetchedRate.getSourceCurrency());
        assertEquals(1.0892, fetchedRate.getExchangeRate());

        verify(restTemplate, times(1)).getForEntity(eq(url), eq(ForexRate.class));
        verify(forexRepository, times(1)).save(any(ForexRate.class));
    }

    @Test
    public void testGetLatestRateFromDatabase() {
        String targetCurrency = "USD";
        ForexRate mockRate = new ForexRate();
        mockRate.setSourceCurrency("EUR");
        mockRate.setTargetCurrency(targetCurrency);
        mockRate.setExchangeRate(1.0892);
        mockRate.setDate(LocalDate.now());

        when(forexRepository.findTopByTargetCurrencyOrderByDateDesc(targetCurrency))
                .thenReturn(Optional.of(mockRate));

        ForexRate latestRate = forexService.getLatestRate(targetCurrency);

        assertNotNull(latestRate);
        assertEquals(targetCurrency, latestRate.getTargetCurrency());
        assertEquals("EUR", latestRate.getSourceCurrency());
        assertEquals(1.0892, latestRate.getExchangeRate());

        verify(forexRepository, times(1)).findTopByTargetCurrencyOrderByDateDesc(targetCurrency);
        verifyNoInteractions(restTemplate);
    }

    @Test
    public void testGetLatestRateFromApi() {
        String targetCurrency = "USD";
        LocalDate today = LocalDate.now();
        String url = "https://api.frankfurter.app/" + today + "?to=" + targetCurrency;

        ForexApiResponse mockApiResponse = new ForexApiResponse();
        mockApiResponse.setBase("EUR");
        mockApiResponse.setDate(today);
        Map<String, Double> rates = new HashMap<>();
        rates.put(targetCurrency, 1.0892);
        mockApiResponse.setRates(rates);

        when(forexRepository.findTopByTargetCurrencyOrderByDateDesc(targetCurrency))
                .thenReturn(Optional.empty());
        when(restTemplate.getForObject(eq(url), eq(ForexApiResponse.class)))
                .thenReturn(mockApiResponse);

        ForexRate latestRate = forexService.getLatestRate(targetCurrency);

        assertNotNull(latestRate);
        assertEquals(targetCurrency, latestRate.getTargetCurrency());
        assertEquals("EUR", latestRate.getSourceCurrency());
        assertEquals(1.0892, latestRate.getExchangeRate());

        verify(forexRepository, times(1)).findTopByTargetCurrencyOrderByDateDesc(targetCurrency);
        verify(restTemplate, times(1)).getForObject(eq(url), eq(ForexApiResponse.class));
        verify(forexRepository, times(1)).save(any(ForexRate.class));
    }

    @Test
    public void testGetLatestRatesFromDatabase() {
        String targetCurrency = "USD";
        ForexRate mockRate = new ForexRate();
        mockRate.setSourceCurrency("EUR");
        mockRate.setTargetCurrency(targetCurrency);
        mockRate.setExchangeRate(1.0892);
        mockRate.setDate(LocalDate.now());

        when(forexRepository.findTopByTargetCurrencyOrderByDateDesc(targetCurrency))
                .thenReturn(Optional.of(mockRate));

        List<ForexRate> latestRates = forexService.getLatestRates(targetCurrency);

        assertNotNull(latestRates);
        assertEquals(1, latestRates.size());
        assertEquals(targetCurrency, latestRates.get(0).getTargetCurrency());
        assertEquals("EUR", latestRates.get(0).getSourceCurrency());
        assertEquals(1.0892, latestRates.get(0).getExchangeRate());

        verify(forexRepository, times(1)).findTopByTargetCurrencyOrderByDateDesc(targetCurrency);
        verifyNoInteractions(restTemplate);
    }

    @Test
    public void testGetLatestRatesFromApi() {
        LocalDate today = LocalDate.now();
        String url = "https://api.frankfurter.app/latest";
        ForexApiResponseList mockApiResponseList = new ForexApiResponseList();
        mockApiResponseList.setBase("EUR");
        mockApiResponseList.setDate(today);
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0892);
        rates.put("GBP", 0.8426);
        rates.put("JPY", 167.61);
        rates.put("CZK", 25.429);
        mockApiResponseList.setRates(rates);

        when(forexRepository.findTopByTargetCurrencyOrderByDateDesc(anyString()))
                .thenReturn(Optional.empty());
        when(restTemplate.getForObject(eq(url), eq(ForexApiResponseList.class)))
                .thenReturn(mockApiResponseList);

        List<ForexRate> latestRates = forexService.getLatestRates(null);

        assertNotNull(latestRates);
        assertEquals(4, latestRates.size());

        verify(forexRepository, times(4)).findTopByTargetCurrencyOrderByDateDesc(anyString());
        verify(restTemplate, times(1)).getForObject(eq(url), eq(ForexApiResponseList.class));
        verify(forexRepository, times(4)).save(any(ForexRate.class));
    }
}