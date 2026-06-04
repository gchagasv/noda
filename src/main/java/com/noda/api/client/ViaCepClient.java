package com.noda.api.client;

import com.noda.api.dtos.ViaCepResponseDTO;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ViaCepClient {

    private final RestClient restClient;

    public ViaCepClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("https://viacep.com.br/ws").build();
    }

    public ViaCepResponseDTO getAddressByCep(String cep) {
        return restClient.get()
                .uri("/{cep}/json/", cep)
                .retrieve()
                .body(ViaCepResponseDTO.class);
    }
}