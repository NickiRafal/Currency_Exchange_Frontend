package com.kodilla.currency_exchange_frontend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyController {
    private final RestTemplate restTemplate;

    @Autowired
    public CurrencyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }



    @GetMapping("/getCurrencyData")
    public String getCurrencyData() {
        String apiUrl = "http://api.nbp.pl/api/exchangerates/tables/C/"; // Tu podaj adres API

        // Pobierz dane z zewnętrznego API
        return restTemplate.getForObject(apiUrl, String.class);
    }

    @PostMapping("/saveCurrencyData")
    public String saveCurrencyData(
            @RequestParam("currentRates") String currentRates,
            @RequestParam("historicalRates") String historicalRates,
            @RequestParam("information") String information) {

        // Tutaj możesz wywołać serwis lub repozytorium do zapisu danych do bazy danych
        System.out.println("Aktualne kursy: " + currentRates);
        System.out.println("Historyczne kursy: " + historicalRates);
        System.out.println("Informacje: " + information);

        return "redirect:/"; // Możesz przekierować użytkownika gdziekolwiek chcesz
    }
}
