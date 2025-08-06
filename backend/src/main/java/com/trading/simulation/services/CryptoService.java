package com.trading.simulation.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.simulation.repositories.CryptoRepository;

@Service
public class CryptoService {
     @Autowired
    private CryptoRepository cryptoRepo;
     public List<Map<String, Object>> getCryptos() {
        return cryptoRepo.getCryptos();
    }
}
