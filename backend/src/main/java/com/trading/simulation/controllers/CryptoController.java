package com.trading.simulation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.trading.simulation.services.CryptoService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cryptos")
@CrossOrigin("http://localhost:3000")
public class CryptoController {

    @Autowired
    private CryptoService cryptoService;

    @GetMapping
    public List<Map<String, Object>> getCryptos() { 
        return cryptoService.getCryptos();
    }
}

