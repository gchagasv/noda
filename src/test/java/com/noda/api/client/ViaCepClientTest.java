package com.noda.api.client;

import com.noda.api.dtos.ViaCepResponseDTO;
import com.noda.api.exceptions.CepNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(MockitoExtension.class)
public class ViaCepClientTest {

    private ViaCepClient viaCepClient;
    private MockRestServiceServer server;

    @BeforeEach // test pollution
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        viaCepClient = new ViaCepClient(builder);
    }

    @Test
    @DisplayName("Should successfully parse ViaCep JSON response when CEP is valid")
    void shouldReturnResponseWhenCepIsValid() {
        String targetCep = "80220450";
        String mockJsonResponse = """
                {
                    "cep": "80220450",
                    "logradouro": "Av. Presidente Vargas",
                    "bairro": "Centro",
                    "localidade": "Esteio",
                    "uf": "RS"
                }
                """;

        server.expect(requestTo("https://viacep.com.br/ws/" + targetCep + "/json/"))
                .andRespond(withSuccess(mockJsonResponse, MediaType.APPLICATION_JSON));

        ViaCepResponseDTO result = viaCepClient.getAddressByCep(targetCep);

        assertNotNull(result);
        assertEquals("Av. Presidente Vargas", result.logradouro());
        assertEquals("Esteio", result.localidade());
        assertEquals("RS", result.uf());
    }
    @Test
    @DisplayName("Should throw CepNotFoundException when cep is not valid")
    void shouldThrowCepNotFoundException() {
        String testInvalidCep = "99966655588";

        server.expect(requestTo("https://viacep.com.br/ws/" + testInvalidCep + "/json/"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        CepNotFoundException exception = assertThrows(CepNotFoundException.class, () -> {
            viaCepClient.getAddressByCep(testInvalidCep);
        });
        assertEquals("CEP not found: " + testInvalidCep, exception.getMessage());
    }
}