package com.noda.api.services;

import com.noda.api.client.ViaCepClient;
import com.noda.api.dtos.ViaCepResponseDTO;
import com.noda.api.exceptions.CepNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ViaCepService {

    private final ViaCepClient viaCepClient;

    public ViaCepService(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    public ViaCepResponseDTO fetchAddressByCep(String cep) {
        ViaCepResponseDTO response;

        try {
            response = viaCepClient.getAddressByCep(cep);
        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with ViaCEP API", e);
        }

        if (response == null || Boolean.TRUE.equals(response.erro())) {
            throw new CepNotFoundException("CEP not found: " + cep);
        }

        return response;
    }
}