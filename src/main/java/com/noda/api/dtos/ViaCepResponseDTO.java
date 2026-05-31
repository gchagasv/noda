package com.noda.api.dtos;

public record ViaCepResponseDTO(
        String logradouro,
        String bairro,
        String localidade,
        String uf,
        Boolean erro
) {}
