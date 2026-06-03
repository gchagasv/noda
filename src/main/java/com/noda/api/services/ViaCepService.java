package com.noda.api.services;


import com.noda.api.dtos.ViaCepResponseDTO;
import com.noda.api.exceptions.CepNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ViaCepService {

    private final RestClient restClient = RestClient.create("https://viacep.com.br/ws");

    public ViaCepResponseDTO fetchAddressByCep(String cep) {
        ViaCepResponseDTO response;

        try {
            response = restClient.get()
                    .uri("/{cep}/json/", cep)
                    .retrieve()
                    .body(ViaCepResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with ViaCEP API", e);
        }

        if (response == null || Boolean.TRUE.equals(response.erro())) {
            throw new CepNotFoundException("CEP not found: " + cep);
        }
        return response;
    }
}