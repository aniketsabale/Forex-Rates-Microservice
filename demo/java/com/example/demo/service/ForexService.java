package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.controller.ForexController;
import com.example.demo.entity.ForexApiResponse;
import com.example.demo.entity.ForexApiResponseList;
import com.example.demo.entity.ForexRate;
import com.example.demo.repository.ForexRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ForexService {
	private static final Logger logger = LoggerFactory.getLogger(ForexService.class);
    private final RestTemplate restTemplate;
    private final ForexRepository forexRepository;
    private final String FetchLetestURL = "https://api.frankfurter.app/latest?to=";
    private final String FetchtargetCurrencyURL = "https://api.frankfurter.app/";

    @Autowired
    public ForexService(RestTemplate restTemplate, ForexRepository forexRepository) {
        this.restTemplate = restTemplate;
        this.forexRepository = forexRepository;
    }

    public ForexRate fetchLatestRate(String targetCurrency) {
    	
        String url = FetchLetestURL + targetCurrency;
        logger.info("Calling URl {}",url);
        ResponseEntity<ForexRate> response = restTemplate.getForEntity(url, ForexRate.class);
        ForexRate rate = response.getBody();
        forexRepository.save(rate);
        return rate;
    }
    public ForexRate getLatestRate(String targetCurrency) {
        LocalDate today = LocalDate.now();

        // Check if the rate is present in the database
        Optional<ForexRate> optionalRate = forexRepository.findTopByTargetCurrencyOrderByDateDesc(targetCurrency);

        if (optionalRate.isPresent()) {
            return optionalRate.get();
        } else {
            // Fetch rate from the external API
            String url = FetchtargetCurrencyURL + today + "?to=" + targetCurrency;
            System.out.println("Calling url: "+url);
            
            ForexApiResponse apiResponse = restTemplate.getForObject(url, ForexApiResponse.class);
            System.out.println("ForexApiResponse "+apiResponse);
            
            if (apiResponse != null && apiResponse.getRates() != null && apiResponse.getRates().containsKey(targetCurrency)) {
                ForexRate apiRate = new ForexRate();
                apiRate.setSourceCurrency(apiResponse.getBase());
                apiRate.setTargetCurrency(targetCurrency);
                apiRate.setExchangeRate(apiResponse.getRates().get(targetCurrency));
                apiRate.setDate(apiResponse.getDate());
                System.out.println("Save the fetched rate to the database");
                // Save the fetched rate to the database
                forexRepository.save(apiRate);

                return apiRate;
            }

            return null;
        }
    }

    
  
    public List<ForexRate> getLatestRates(String targetCurrency) {
        List<ForexRate> forexRates = new ArrayList<>();
       
        // Check if the rate is present in the database
        if (targetCurrency != null && !targetCurrency.isEmpty()) {
            Optional<ForexRate> optionalRate = forexRepository.findTopByTargetCurrencyOrderByDateDesc(targetCurrency);
            optionalRate.ifPresent(forexRates::add);
        } else {
            // Fetch rates for USD to EUR, GBP, JPY, CZK
            String[] targetCurrencies = {"EUR", "GBP", "JPY", "CZK"};
            for (String currency : targetCurrencies) {
                Optional<ForexRate> optionalRate = forexRepository.findTopByTargetCurrencyOrderByDateDesc(currency);
                optionalRate.ifPresent(forexRates::add);
            }
        }

        if (!forexRates.isEmpty()) {
            return forexRates;
        } else {
            // Fetch rates from the external API
            String url = "https://api.frankfurter.app/latest";
            logger.info("Calling URl {}",url);
            ForexApiResponseList apiResponse = restTemplate.getForObject(url, ForexApiResponseList.class);

            if (apiResponse != null && apiResponse.getRates() != null) {
                for (Map.Entry<String, Double> entry : apiResponse.getRates().entrySet()) {
                    ForexRate apiRate = new ForexRate();
                    apiRate.setSourceCurrency(apiResponse.getBase());
                    apiRate.setTargetCurrency(entry.getKey());
                    apiRate.setExchangeRate(entry.getValue());
                    apiRate.setDate(apiResponse.getDate());

                    // Save the fetched rate to the database
                    forexRepository.save(apiRate);
                    forexRates.add(apiRate);
                }
            }
        }

        return forexRates;
    }
    

    
    
}